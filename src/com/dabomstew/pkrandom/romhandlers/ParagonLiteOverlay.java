package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.arm.ArmParser;

import java.io.IOException;
import java.util.*;
import java.util.zip.DataFormatException;

public class ParagonLiteOverlay {
    private final int number;
    private byte[] data;
    private int address;

    LinkedList<FreeSpace> freeSpaces = new LinkedList<>();

    private static class FreeSpace {
        int start;
        int end;

        FreeSpace(int start, int end) {
            this.start = alignWord(start);
            this.end = alignNextWord(end);
        }

        int size() {
            return end - start;
        }

        @Override
        public String toString() {
            return String.format("[0x%08X-0x%08X) Size: %d", start, end, size());
        }
    }
    
    public int allocate(int size) {
        size = alignNextWord(size);

        Iterator<FreeSpace> iterator = freeSpaces.iterator();

        int maxSize = size;
        FreeSpace largestFreeSpace = null;

        // Try for perfect match
        while (iterator.hasNext()) {
            FreeSpace freeSpace = iterator.next();
            if (freeSpace.size() == size) {
                int start = freeSpace.start;
                iterator.remove();
                return start;
            }

            if (freeSpace.size() > maxSize) {
                maxSize = freeSpace.size();
                largestFreeSpace = freeSpace;
            }
        }

        if (largestFreeSpace != null) {
            largestFreeSpace.end -= size;
            return largestFreeSpace.end;
        }

        return allocateAtFront(size);
    }

    public int allocateAtFront(int size) {
        size = alignNextWord(size);

        int allocAddress = address - size;

        // Check if first Free Space is at start
        if (!freeSpaces.isEmpty()) {
            FreeSpace freeSpace = freeSpaces.getFirst();
            if (freeSpace.start == address) {
                allocAddress += freeSpace.size();
                freeSpace.end -= size;

                if (freeSpace.end <= freeSpace.start)
                    freeSpaces.removeFirst();
            }
        }

        // Expand
        if (allocAddress < address) {
            setAddress(allocAddress);
        }

        return allocAddress;
    }

    public void freeFunc(int address) {
        free(address, getFuncSize(address));
    }

    public void free(int address, int size) {
        address = alignWord(address);
        size = alignNextWord(size);
        
        fill(address, size, (byte) 0x00);

        int funcEnd = address + size;

        if (freeSpaces.isEmpty()) {
            freeSpaces.add(new FreeSpace(address, funcEnd));
            return;
        }

        ListIterator<FreeSpace> iterator = freeSpaces.listIterator();
        while (iterator.hasNext()) {
            FreeSpace freeSpace = iterator.next();

            // Case: Deallocate at end of known free space
            if (address == freeSpace.end) {
                freeSpace.end += size;

                if (!iterator.hasNext())
                    return;

                // Case: Deallocation perfectly spans the free spaces
                FreeSpace nextFreeSpace = iterator.next();
                if (nextFreeSpace.start <= freeSpace.end)
                    iterator.remove();

                return;
            }

            // Case: Func is after this free space
            if (address > freeSpace.end) {
                if (iterator.hasNext())
                    continue;

                // Case: There is no next free space
                iterator.add(new FreeSpace(address, funcEnd));
                return;
            }

            // Case: Func ends at start of next free space
            if (funcEnd == freeSpace.start) {
                freeSpace.start = address;
                return;
            }

            // Case: Func starts and ends before this free space
            iterator.previous();
            iterator.add(new FreeSpace(address, funcEnd));
            return;
        }
    }

    public ParagonLiteOverlay(int number, byte[] data, int address) {
        this.number = number;
        this.data = data;
        this.address = address;
    }

    public  int getNumber() {
        return number;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int newAddress) {
        // Align 4
        newAddress &= 0xFFFFFFFC;

        int diff = address - newAddress;

        int newLength = data.length + diff;
        byte[] newOverlayData = new byte[newLength];
        System.arraycopy(data, 0, newOverlayData, diff, data.length);

        data = newOverlayData;
        address = newAddress;
    }

    public void save(Gen5RomHandler romHandler) {
        try {
            // Align 64
            setAddress(address & 0xFFFFFFC0);

            romHandler.setOverlayAddress(number, address);
            romHandler.writeOverlay(number, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        return data.length;
    }

    public int getFuncSize(int ramAddress) {
        try {
            ArmParser armParser = new ArmParser();
            return armParser.getFuncSize(data, ramAddress - address);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
    }

    // Obtains the RAM address of the found string of bytes
    public int find(String hexString) {
        hexString = hexString.replace(" ", "");

        if (hexString.length() % 2 != 0) {
            return -3; // error
        }
        byte[] searchFor = new byte[hexString.length() / 2];
        for (int i = 0; i < searchFor.length; i++) {
            searchFor[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
        List<Integer> found = RomFunctions.search(data, searchFor);
        if (found.isEmpty()) {
            return -1; // not found
        } else if (found.size() > 1) {
            return -2; // not unique
        }

        return found.get(0) + address;
    }

    public void fill(int ramAddress, int length, byte value) {
        int offset = ramAddress - address;
        for (int i = 0; i < length; ++i)
            data[offset + i] = value;
    }

    public void writeByte(int ramAddress, byte byteValue) {
        int offset = ramAddress - address;
        data[offset] = (byte) (byteValue & 0xFF);
    }

    public void writeByte(int ramAddress, int byteValue) {
        int offset = ramAddress - address;
        data[offset] = (byte) (byteValue & 0xFF);
    }

    public void writeHalfword(int ramAddress, int halfword) {
        int offset = ramAddress - address;
        data[offset] = (byte) (halfword & 0xFF);
        data[offset + 1] = (byte) ((halfword >> 8) & 0xFF);
    }

    public void writeWord(int ramAddress, int word) {
        int offset = ramAddress - address;
        data[offset] = (byte) (word & 0xFF);
        data[offset + 1] = (byte) ((word >> 8) & 0xFF);
        data[offset + 2] = (byte) ((word >> 16) & 0xFF);
        data[offset + 3] = (byte) ((word >> 24) & 0xFF);
    }

    public void writeBytes(int ramAddress, byte[] bytes) {
        System.arraycopy(bytes, 0, data, ramAddress - address, bytes.length);
    }

    public byte readByte(int ramAddress) {
        int offset = ramAddress - address;
        return data[offset];
    }

    public int readUnsignedByte(int ramAddress) {
        int offset = ramAddress - address;
        return ((int) data[offset]) & 0xFF;
    }

    public int readHalfword(int ramAddress) {
        int offset = ramAddress - address;
        return (short) (((data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)));
    }

    public int writeArm(List<String> lines, ArmParser armParser) {
        int size = armParser.getByteLength(lines);
        int address = allocate(size);

        byte[] bytes = armParser.parse(lines, address);
        writeBytes(address, bytes);

        return address;
    }

    public int readUnsignedHalfword(int ramAddress) {
        int offset = ramAddress - address;
        return ((data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8));
    }

    public int readWord(int ramAddress) {
        int offset = ramAddress - address;
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    public void copyBytes(int sourceRamAddress, int destinationRamAddress, int length) {
        System.arraycopy(data, sourceRamAddress - address, data, destinationRamAddress - address, length);
    }

    public static int alignWord(int address) {
        return address & 0xFFFFFFFC;
    }

    public static int alignNextWord(int address) {
        return alignWord(address + 3);
    }
}
