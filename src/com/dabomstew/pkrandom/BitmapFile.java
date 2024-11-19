package com.dabomstew.pkrandom;

import java.util.zip.DataFormatException;

public class BitmapFile {
    int width;
    int height;

    int[] colorTable;
    byte[][] pixels;

    public BitmapFile(byte[] data) throws DataFormatException {
        // Read File Header
        String magic = readString(data, 0x00, 2);
        if (!magic.equals("BM"))
            throw new DataFormatException("Not a bitmap file");

        int fileSize = readLong(data, 0x02);
        if (data.length != fileSize)
            throw new DataFormatException(String.format("Reported a file size of %d bytes but the actual size was %d", fileSize, data.length));

        int fileOffsetToPixelArray = readLong(data, 0x0A);

        // Read DIB Header
        int dibHeaderSize = readLong(data, 0x0E);
        if (dibHeaderSize != 40)
            throw new DataFormatException(String.format("Expected a header size of 40 (BITMAPINFOHEADER Windows NT format), got %d instead", dibHeaderSize));

        width = readLong(data, 0x12);
        height = readLong(data, 0x16);
        int planes = readUnsignedWord(data, 0x1A);
        if (planes != 1)
            throw new DataFormatException(String.format("Expected a plane count of 1, got %d instead", planes));

        int bitsPerPixel = readUnsignedWord(data, 0x1C);
        if (bitsPerPixel != 4)
            throw new DataFormatException(String.format("Expected a bits per pixel value of 4, got %d instead", bitsPerPixel));

        int compression = readLong(data, 0x1E);
        if (compression != 0)
            throw new DataFormatException(String.format("Expected a compression value of 0 (BI_RGB), got %d instead", compression));

        int imageSize = readLong(data, 0x22);
        int colorsInColorTable = readLong(data, 0x2E);

        // Read Color Table
        colorTable = new int[colorsInColorTable];
        for (int i = 0; i < colorsInColorTable; ++i) {
            colorTable[i] = readLong(data, 54 + i * 4);
        }

        pixels = new byte[height][width];
        for (int i = 0; i < imageSize; ++i) {
            byte b = data[fileOffsetToPixelArray + i];

            int c1 = (b >> 4) & 0x0F;
            int c2 = b & 0x0F;

            setPixelColor(i * 2, c1);
            setPixelColor(i * 2 + 1, c2);
        }
    }

    private void setPixelColor(int pixelIndex, int colorIndex) {
        int row1 = height - (pixelIndex / width) - 1;
        int col1 = pixelIndex % width;
        pixels[row1][col1] = (byte) colorIndex;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixelColorIndex(int pixelIndex) {
        int row = pixelIndex / width;
        int col = pixelIndex % width;
        return getPixelColorIndex(row, col);
    }

    public int getPixelColorIndex(int row, int col) {
        return pixels[row][col];
    }

    public byte[] writePaletteFile() {
        return writePaletteFileFromColors(colorTable);
    }
    
    public static byte[] writePaletteFileFromColors(int[] colors) {
        byte[] bytes = new byte[552];

        // NCLR Header
        writeString(bytes, 0x00, "RLCN");
        writeLong(bytes, 0x04, 0x0100FEFF);
        writeLong(bytes, 0x08, bytes.length);
        writeWord(bytes, 0x0C, 16);
        writeWord(bytes, 0x0E, 1);

        // PLTT Header
        writeString(bytes, 0x10, "TTLP");
        writeLong(bytes, 0x14, bytes.length - 0x10);
        writeLong(bytes, 0x18, 3);
        writeLong(bytes, 0x20, 0x200);
        writeLong(bytes, 0x24, 16);
        
        // PLTT Color Array
        int colorArrayOffset = 0x28;
        writeWord(bytes, colorArrayOffset, 0x5AD6); // index 0 is always ignored as it is intended to be transparency
        for (int i = 0; i < colors.length; ++i) {
            int color = colors[i];
            int c = GFXFunctions.convARGBTo16BitColor(color);
            writeWord(bytes, colorArrayOffset + i * 2, c);
        }

        return bytes;
    }

    public static class GraphicsFileParams {
        public int width = 0;
        public int height = 0;
        public int subImageCount = 0;
    }

    public byte[] writeGraphicFile(GraphicsFileParams params) {
        int tileWidth = 8;
        int tileSize = tileWidth * tileWidth;

        int width = params.width > 0 ? params.width : this.width;
        int height = params.height > 0 ? params.height : this.height;
        int subImageCount = params.subImageCount;

        if (width % tileWidth != 0 || height % tileWidth != 0)
            throw new RuntimeException();
        
        int tileCount = (width * height) / tileSize;

        if (subImageCount > 0)
            if ((width * height) % (subImageCount * tileSize) != 0)
                throw new RuntimeException();
        
        int headerSize = 16;
        int charHeaderSize = 32;
        int charSize = charHeaderSize + (width * height / 2);
        int cposSize = subImageCount > 0 ? 16 : 0;
        int fileSize = headerSize + charSize + cposSize;
        byte[] bytes = new byte[fileSize];

        // NCGR Header
        writeString(bytes, 0x00, "RGCN");
        writeWord(bytes, 0x04, 0xFEFF); // BOM
        writeLong(bytes, 0x06, 0x0101); // const
        writeLong(bytes, 0x08, fileSize); // file size
        writeWord(bytes, 0x0C, headerSize); // header size
        writeWord(bytes, 0x0E, subImageCount > 0 ? 2 : 1); // section count

        // CHAR Header
        writeString(bytes, 0x10, "RAHC");
        writeLong(bytes, 0x14, charSize);
        if (subImageCount > 0) {
            writeWord(bytes, 0x18, height / tileWidth);
            writeWord(bytes, 0x1A, width / tileWidth);
        } else {
            writeLong(bytes, 0x18, 0xFFFFFFFF);
        }
        writeLong(bytes, 0x1C, 3); // bpp: 3=4bpp; 4=8bpp
        writeLong(bytes, 0x20, subImageCount > 0 ? 0 : 16); // unknown; not sure if this is even due to subImageChunkCount
        writeLong(bytes, 0x28, width * height / 2); // tile data size
        writeLong(bytes, 0x2C, 24); // unknown

        int tilesPerRow = (width / tileWidth);
        int pixelArrayOffset = headerSize + charHeaderSize;
        int pixelIndex = 0;
        for (int tile = 0; tile < tileCount; ++tile) {
            int tileStartRow = (tile / tilesPerRow) * tileWidth;
            int tileStartCol = (tile % tilesPerRow) * tileWidth;

            for (int tilePixel = 0; tilePixel < tileSize; ++tilePixel) {
                int row = tileStartRow + tilePixel / tileWidth;
                int col = tileStartCol + tilePixel % tileWidth;

                if (row < pixels.length && col < pixels[0].length) {
                    byte colorIndex = (byte) getPixelColorIndex(row, col);
                    if (pixelIndex % 2 == 1) {
                        colorIndex <<= 4;
                    }

                    int offset = pixelArrayOffset + pixelIndex / 2;
                    bytes[offset] |= colorIndex;
                }
                
                ++pixelIndex;
            }
        }

        // CPOS
        if (subImageCount <= 0)
            return bytes;

        int cposOffset = headerSize + charSize;
        writeString(bytes, cposOffset, "SOPC");
        writeLong(bytes, cposOffset + 0x04, cposSize);
        writeWord(bytes, cposOffset + 0x0C, tileCount / subImageCount);
        writeWord(bytes, cposOffset + 0x0E, subImageCount);
        
        return bytes;
    }

    private static int readUnsignedWord(byte[] data, int offset) {
        return ((data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8));
    }

    private static void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private static int readLong(byte[] data, int offset) {
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    private static void writeLong(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

    private static String readString(byte[] data, int offset, int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            sb.append((char) data[offset + i]);
        }

        return sb.toString();
    }

    private static void writeString(byte[] data, int offset, String str) {
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            char c = charArray[i];
            if (c == '\0')
                return;

            data[offset + i] = (byte) c;
        }
    }
}
