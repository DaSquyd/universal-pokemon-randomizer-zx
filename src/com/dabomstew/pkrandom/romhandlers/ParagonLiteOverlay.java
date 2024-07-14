package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.arm.ArmParser;

import java.io.IOException;
import java.util.*;

public class ParagonLiteOverlay {
    protected final int number;
    protected final String name;
    protected byte[] data;
    protected int address;
    protected Insertion insertion;
    protected ParagonLiteAddressMap globalAddressMap;
    ArmParser armParser;

    LinkedList<FreeSpace> freeSpaces = new LinkedList<>();

    public static enum Insertion {
        Front,
        Back
    }

    protected static class FreeSpace {
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

    public ParagonLiteOverlay(int number, String name, byte[] data, int address, Insertion insertion, ParagonLiteAddressMap globalAddressMap) {
        this.number = number;
        this.name = name;
        this.data = data;
        this.address = address;
        this.insertion = insertion;
        this.globalAddressMap = globalAddressMap;
        this.armParser = new ArmParser(globalAddressMap);

        globalAddressMap.registerOverlay(this);
    }

    private int allocate(int size) {
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

            if (alignWord(largestFreeSpace.end) != largestFreeSpace.end)
                throw new RuntimeException();

            return largestFreeSpace.end;
        }

        return allocateExtend(size);
    }

    protected int allocateExtend(int size) {
        switch (insertion) {
            case Front:
                return allocateExtendFront(size);
            case Back:
                return allocateExtendBack(size);
            default:
                throw new RuntimeException();
        }
    }

    private int allocateExtendFront(int size) {
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

    private int allocateExtendBack(int size) {
        size = alignNextWord(size);

        int allocAddress = address + data.length;

        if (!freeSpaces.isEmpty()) {
            FreeSpace freeSpace = freeSpaces.getLast();
            if (freeSpace.end == allocAddress) {
                allocAddress = freeSpace.start;
                freeSpace.start = allocAddress + size;

                if (freeSpace.end <= freeSpace.start)
                    freeSpaces.removeFirst();
            }
        }

        int newLength = allocAddress + size - address;

        if (newLength > data.length) {
            byte[] newData = new byte[newLength];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }

        return allocAddress;
    }

    private void free(int address, int size) {
        if (size < 0)
            throw new RuntimeException();

        if (size == 0)
            return;

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

    public int getNumber() {
        return number;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int newAddress) {
        if (!isWordAligned(newAddress))
            throw new RuntimeException(String.format("Address 0x%08X was not word-aligned", newAddress));

        int diff = address - newAddress;

        int newLength = data.length + diff;
        byte[] newOverlayData = new byte[newLength];
        System.arraycopy(data, 0, newOverlayData, diff, data.length);

        data = newOverlayData;
        address = newAddress;
    }

    public void save(Gen5RomHandler romHandler) {
        // Align 32
        setAddress(address & 0xFFFFFFE0);

        romHandler.setOverlayAddress(number, address);

        try {
            romHandler.writeOverlay(number, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        return data.length;
    }

    public int getFuncSize(int ramAddress) {
        return armParser.getFuncSize(data, ramAddress - address);
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

    private void fill(int ramAddress, int length, byte value) {
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

    public void writeWord(int address, int word, boolean isReference) {
        if (isReference) {
            int oldWord = readWord(address);

            int oldDestination = alignWord(oldWord);

            if (word > 0) {
                int newDestination = alignWord(word);
                globalAddressMap.addReference(newDestination, this, address);
            }

            ParagonLiteAddressMap.RemovalData removalData = globalAddressMap.removeReference(oldDestination, this, address);
            if (removalData != null && removalData.referenceCount == 0) {
                free(removalData.address, removalData.size);
            }
        }

        int offset = address - this.address;
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

    public short readSignedHalfword(int ramAddress) {
        int offset = ramAddress - address;
        return (short) (((data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)));
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

    public int writeCode(List<String> lines, String label) {
        int size = armParser.getByteLength(lines);
        int address = allocate(size);

        return writeCodeInternal(lines, address, label);
    }

    public int writeCodeUnnamed(List<String> lines) {
        int size = armParser.getByteLength(lines);
        int address = allocate(size);

        String label = String.format("Code_0x%08X", address);
        return writeCodeInternal(lines, address, label);
    }

    private int writeCodeInternal(List<String> lines, int address, String label) {
        byte[] bytes = armParser.parse(lines, this, address);
        System.arraycopy(bytes, 0, data, address - this.address, bytes.length);

        globalAddressMap.registerCodeAddress(this, label, address, 2);
        return address;
    }

    public void writeCodeForceInline(List<String> lines, String label) {
        int address = globalAddressMap.getAddress(this, label);
        int oldSize = getFuncSize(address);

        byte[] bytes = armParser.parse(lines, this, address);
        if (bytes.length > oldSize)
            throw new RuntimeException("Too large!");

        Map<Integer, Set<Integer>> oldOutgoingReferencesMap = armParser.getOutgoingCodeReferences(data, address - this.address, this.address);
        Map<Integer, Set<Integer>> newOutgoingReferencesMap = armParser.getOutgoingCodeReferences(bytes, 0, this.address);

        // Add new references
        for (Map.Entry<Integer, Set<Integer>> entry : newOutgoingReferencesMap.entrySet()) {
            int destinationAddress = entry.getKey();
            for (int sourceAddress : entry.getValue())
                globalAddressMap.addReference(this, destinationAddress, sourceAddress);
        }

        // Remove old references not in new references
        for (Map.Entry<Integer, Set<Integer>> entry : oldOutgoingReferencesMap.entrySet()) {
            int destinationAddress = entry.getKey();

            // Remove all if destination is not found in new references
            if (!newOutgoingReferencesMap.containsKey(destinationAddress)) {
                for (int sourceAddress : entry.getValue()) {
                    oldOutgoingReferencesMap.get(destinationAddress).remove(sourceAddress);
                    globalAddressMap.removeReference(destinationAddress, this, sourceAddress);
                }

                continue;
            }

            Set<Integer> newSources = newOutgoingReferencesMap.get(destinationAddress);
            for (int sourceAddress : entry.getValue()) {
                if (!newSources.contains(sourceAddress)) {
                    globalAddressMap.removeReference(destinationAddress, this, sourceAddress);
                }
            }
        }

        writeBytes(address, bytes);

        int diff = oldSize - bytes.length;
        free(address + bytes.length, diff);
    }

    public int replaceCode(List<String> lines, String label) {
        int size = armParser.getByteLength(lines);
        int address = allocate(size);
        
        byte[] bytes = armParser.parse(lines, this, address);
        System.arraycopy(bytes, 0, data, address - this.address, bytes.length);

        globalAddressMap.relocateCodeAddress(this, label, address);
        return address;
    }

    public int writeData(byte[] bytes, String label) {
        return writeData(bytes, label, null);
    }

    public int writeData(byte[] bytes, String label, String refPattern) {
        int address = allocate(bytes.length);
        return writeDataInternal(bytes, label, address, refPattern);
    }

    public int writeDataUnnamed(byte[] bytes) {
        return writeDataUnnamed(bytes, null);
    }

    public int writeDataUnnamed(byte[] bytes, String refPattern) {
        int address = allocate(bytes.length);
        String label = String.format("Data_0x%08X", address);

        return writeDataInternal(bytes, label, address, refPattern);
    }

    private int writeDataInternal(byte[] bytes, String label, int address, String refPattern) {
        System.arraycopy(bytes, 0, data, address - this.address, bytes.length);

        globalAddressMap.registerDataAddress(this, label, address, bytes.length, refPattern);
        return address;
    }

    public int newData(byte[] bytes, String label, String refPattern) {
        int address = allocate(bytes.length);
        System.arraycopy(bytes, 0, data, address - this.address, bytes.length);

        globalAddressMap.relocateDataAddress(this, label, address, bytes.length, refPattern);
        return address;
    }

    public static boolean isWordAligned(int address) {
        return alignWord(address) == address;
    }

    public static int alignWord(int address) {
        return address & 0xFFFFFFFC;
    }

    public static int alignNextWord(int address) {
        return alignWord(address + 3);
    }
}
