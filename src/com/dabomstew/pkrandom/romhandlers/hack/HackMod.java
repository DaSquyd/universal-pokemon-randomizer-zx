package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.FormeInfo;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class HackMod implements Comparable<HackMod> {

    public static class Context {
        public Gen5RomHandler romHandler;
        public Settings settings;

        public ArmParser armParser;
        public ParagonLiteAddressMap globalAddressMap;

        public ParagonLiteArm9 arm9;
        public Map<OverlayId, ParagonLiteOverlay> overlays;

        public Pokemon[] classicPokes;
        public Pokemon[] pokes;
        public Map<Integer, FormeInfo> formeMappings;
        public List<Move> moves;
        
        public NARCArchive moveAnimationsNarc;
        public int originalMoveAnimationsNarcCount;
        public NARCArchive moveAnimationScriptsNarc;
        public NARCArchive battleAnimationScriptsNarc;

        public Map<Class<? extends HackMod>, HackMod> applied;
    }

    public static class HackModComparator implements Comparator<HackMod> {
        @Override
        public int compare(HackMod o1, HackMod o2) {
            return o1.compareTo(o2);
        }
    }

    public abstract Set<Class<? extends HackMod>> getDependencies();

    public abstract void registerGlobalValues(Context context);
    
    public abstract void apply(Context context);

    public abstract void Merge(HackMod other);
    
    @Override
    public int compareTo(HackMod o) {
        int comparison = Integer.compare(getDependencies().size(), o.getDependencies().size());
        if (comparison != 0)
            return comparison;

        return getClass().getName().compareToIgnoreCase(o.getClass().getName());
    }

    protected static List<String> readLines(String filename) {
        Scanner sc;
        try {
            filename = "paragonlite/" + filename;
            InputStream stream = FileFunctions.openConfig(filename);
            if (stream == null)
                throw new RuntimeException(String.format("Could not find file \"%s\"", filename));
            sc = new Scanner(stream, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<String> lines = new ArrayList<>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }

        return lines;
    }

    protected static byte[] readBytes(String filename) {
        byte[] bytes;
        try {
            InputStream stream = FileFunctions.openConfig("paragonlite/" + filename);

            if (stream == null)
                throw new IOException(String.format("Could not find file with name '%s'", filename));

            bytes = new byte[stream.available()];
            if (stream.read(bytes) == -1)
                throw new IOException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    protected void addGlobalValue(Context context, String name, int value) {
        context.armParser.addGlobalValue(name, value);
    }

    protected void addGlobalValue(Context context, String name, double value) {
        context.armParser.addGlobalValue(name, value);
    }

    protected void addGlobalValue(Context context, String name, boolean value) {
        context.armParser.addGlobalValue(name, value);
    }

    protected static void writeBit(byte[] data, int byteOffset, int bitOffset, boolean value) {
        writeBits(data, byteOffset, bitOffset, 1, value ? 1 : 0);
    }

    protected static void writeBits(byte[] data, int byteOffset, int bitOffset, int bitLength, int value) {
        if (bitLength + bitOffset > 8)
            throw new RuntimeException(String.format("Could not fit value of length %d at offset %d", bitLength, bitOffset));

        byte b = (byte) writeBitsHelper(data[byteOffset], bitOffset, bitLength, value);
        data[byteOffset] = b;
    }

    protected static void writeHalf(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    protected static void writeHalfBit(byte[] data, int byteOffset, int bitOffset, boolean value) {
        writeHalfBits(data, byteOffset, bitOffset, 1, value ? 1 : 0);
    }

    protected static void writeHalfBits(byte[] data, int byteOffset, int bitOffset, int bitLength, int value) {
        if (bitLength + bitOffset > 16)
            throw new RuntimeException(String.format("Could not fit value of length %d at offset %d", bitLength, bitOffset));

        int half = writeBitsHelper(readHalf(data, byteOffset), bitOffset, bitLength, value);
        writeHalf(data, byteOffset, half);
    }

    protected static int readHalf(byte[] data, int offset) {
        return data[offset] | (data[offset + 1] << 8);
    }

    protected static void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

    protected static void writeWordBit(byte[] data, int byteOffset, int bitOffset, boolean value) {
        writeWordBits(data, byteOffset, bitOffset, 1, value ? 1 : 0);
    }

    protected static void writeWordBits(byte[] data, int byteOffset, int bitOffset, int bitLength, int value) {
        if (bitLength + bitOffset > 32)
            throw new RuntimeException(String.format("Could not fit value of length %d at offset %d", bitLength, bitOffset));

        int word = writeBitsHelper(readWord(data, byteOffset), bitOffset, bitLength, value);
        writeHalf(data, byteOffset, word);
    }

    protected static int readWord(byte[] data, int offset) {
        return data[offset] | (data[offset + 1] << 8) | (data[offset + 1] << 16) | (data[offset + 1] << 24);
    }

    protected static int readWordBits(byte[] data, int byteOffset, int bitOffset, int bitLength) {
        if (bitLength + bitOffset > 32)
            throw new RuntimeException(String.format("Could not fit value of length %d at offset %d", bitLength, bitOffset));

        return readBitsHelper(readWord(data, byteOffset), bitOffset, bitLength);
    }

    protected static int writeBitsHelper(int full, int bitOffset, int bitLength, int value) {
        int mask = (1 << bitLength) - 1;
        if ((Math.abs(value) & mask) != Math.abs(value))
            throw new RuntimeException(String.format("Could not fit value %d in bit length of %d", value, bitLength));

        return (full & ~(mask << bitOffset)) | ((value & mask) << bitOffset);
    }

    protected static int readBitsHelper(int full, int bitOffset, int bitLength) {
        int mask = ((1 << bitLength) - 1) << bitOffset;

        return (full & mask) >> bitLength;
    }
}
