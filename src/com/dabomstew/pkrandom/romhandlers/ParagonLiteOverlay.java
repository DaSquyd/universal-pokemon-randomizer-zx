package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.arm.ArmParser;

import java.io.IOException;
import java.util.*;

public class ParagonLiteOverlay {
    protected Gen5RomHandler romHandler;
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
        Back,
        Restricted
    }

    protected static class FreeSpace {
        int start;
        int end;

        FreeSpace(int start, int end) {
            this.start = align(start, 4);
            this.end = alignNext(end, 4);
        }

        int size() {
            return end - start;
        }

        @Override
        public String toString() {
            return String.format("[0x%08X-0x%08X) Size: %d", start, end, size());
        }
    }

    public ParagonLiteOverlay(Gen5RomHandler romHandler, int number, String name, byte[] data, int address, Insertion insertion, ArmParser armParser, ParagonLiteAddressMap globalAddressMap) {
        this.romHandler = romHandler;
        this.number = number;
        this.name = name;
        this.data = data;
        this.address = address;
        this.insertion = insertion;
        this.globalAddressMap = globalAddressMap;
        this.armParser = armParser;

        globalAddressMap.registerOverlay(this);
    }

    public int getLowerBoundRamAddress() {
        return address;
    }
    
    public int getUpperBoundRamAddress() {
        return address + size();
    }

    public int romToRamAddress(int romAddress) {
        return romAddress;
    }

    public int ramToRomAddress(int romAddress) {
        return romAddress;
    }

    public int allocateRom(int size) {
        return allocateRomRange(size, -1, -1);
    }

    public int allocateRomNear(int size, int referenceAddress, int referenceSize) {
        return allocateRomRange(size, referenceAddress, referenceAddress + referenceSize);
    }

    private boolean rangeIsNear(int startA, int endA, int startB, int endB) {
        if (startA < 0 || endA < 0 || startB < 0 || endB < 0)
            return true;

        int min = 0xFFC00000; // -4194304
        int max = 0x003FFFFF; // +4194303

        int startToStart = startA - startB;
        int startToEnd = startA - endB;
        int endToStart = endA - startB;
        int endToEnd = endA - endB;

        return startToStart >= min && startToStart <= max
                && startToEnd >= min && startToEnd <= max
                && endToStart >= min && endToStart <= max
                && endToEnd >= min && endToEnd <= max;
    }

    private int allocateRomRange(int size, int rangeStart, int rangeEnd) {
        size = alignNext(size, 4);

        Iterator<FreeSpace> iterator = freeSpaces.iterator();

        int maxSize = size;
        FreeSpace largestFreeSpace = null;

        // Try for perfect match
        while (iterator.hasNext()) {
            FreeSpace freeSpace = iterator.next();
            if (!rangeIsNear(romToRamAddress(freeSpace.start), romToRamAddress(freeSpace.end), rangeStart, rangeEnd))
                continue;

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

            if (align(largestFreeSpace.end, 4) != largestFreeSpace.end)
                throw new RuntimeException();

            return largestFreeSpace.end;
        }

        int allocationRomAddress = allocateExtend(size);
        int allocationRamAddress = romToRamAddress(allocationRomAddress);
        if (!rangeIsNear(allocationRamAddress, allocationRamAddress + size, rangeStart, rangeEnd))
            throw new RuntimeException();

        return allocationRomAddress;
    }

    public int allocateExtend(int size) {
        return switch (insertion) {
            case Front -> allocateExtendFront(size);
            case Back -> allocateExtendBack(size);
            case Restricted -> throw new RuntimeException("Cannot allocate on restricted overlay " + name);
        };
    }

    private int allocateExtendFront(int size) {
        size = alignNext(size, 4);

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
        size = alignNext(size, 4);

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

    private void free(int romAddress, int size) {
        if (size < 0)
            throw new RuntimeException();

        if (size == 0)
            return;

        romAddress = align(romAddress, 4);
        size = alignNext(size, 4);

        clear(romAddress, size);

        int funcEnd = romAddress + size;

        if (freeSpaces.isEmpty()) {
            freeSpaces.add(new FreeSpace(romAddress, funcEnd));
            return;
        }

        ListIterator<FreeSpace> iterator = freeSpaces.listIterator();
        while (iterator.hasNext()) {
            FreeSpace freeSpace = iterator.next();

            // Case: Deallocate at end of known free space
            if (romAddress == freeSpace.end) {
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
            if (romAddress > freeSpace.end) {
                if (iterator.hasNext())
                    continue;

                // Case: There is no next free space
                iterator.add(new FreeSpace(romAddress, funcEnd));
                return;
            }

            // Case: Func ends at start of next free space
            if (funcEnd == freeSpace.start) {
                freeSpace.start = romAddress;
                return;
            }

            // Case: Func starts and ends before this free space
            iterator.previous();
            iterator.add(new FreeSpace(romAddress, funcEnd));
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
        if (!isAligned(newAddress, 4))
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
        setAddress(align(address, 32));

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

    public int getFuncSizeRam(int ramAddress) {
        int romAddress = ramToRomAddress(ramAddress);
        return getFuncSizeRom(romAddress);
    }

    public int getFuncSizeRom(int romAddress) {
        return armParser.getFuncSize(this, romAddress);
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

    private void clear(int romAddress, int length) {
        int offset = romAddress - address;
        for (int i = 0; i < length; ++i)
            data[offset + i] = (byte) 0x00;
    }

    public void writeByte(int romAddress, byte byteValue) {
        int offset = romAddress - address;
        data[offset] = (byte) (byteValue & 0xFF);
    }

    public void writeByte(int romAddress, int byteValue) {
        int offset = romAddress - address;
        data[offset] = (byte) (byteValue & 0xFF);
    }

    public void writeHalfword(int romAddress, int halfword) {
        int offset = romAddress - address;
        data[offset] = (byte) (halfword & 0xFF);
        data[offset + 1] = (byte) ((halfword >> 8) & 0xFF);
    }

    public void writeWord(int romAddress, int word, boolean isReference) {
        if (isReference) {
            int ramAddress = romToRamAddress(romAddress);
            int oldWord = readWord(romAddress);

            int oldDestination = align(oldWord, 4);

            if (word > 0) {
                int newDestination = align(word, 4);
                globalAddressMap.addReference(newDestination, this, ramAddress);
            }

            ParagonLiteAddressMap.RemovalData removalData = globalAddressMap.removeReference(oldDestination, this, ramAddress);
        }

        int offset = romAddress - this.address;

        if (offset < 0 || offset + 3 > data.length)
            throw new RuntimeException();

        data[offset] = (byte) (word & 0xFF);
        data[offset + 1] = (byte) ((word >> 8) & 0xFF);
        data[offset + 2] = (byte) ((word >> 16) & 0xFF);
        data[offset + 3] = (byte) ((word >> 24) & 0xFF);
    }

    public void writeBytes(int romAddress, byte[] bytes) {
        System.arraycopy(bytes, 0, data, romAddress - address, bytes.length);
    }

    public int readUnsignedByte(int romAddress) {
        int offset = romAddress - address;
        return ((int) data[offset]) & 0xFF;
    }

    public int readUnsignedHalfword(int romAddress) {
        int offset = romAddress - address;
        if (offset < 0 || offset + 1 > data.length)
            throw new RuntimeException();

        return ((data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8));
    }

    public int readWord(int romAddress) {
        int offset = romAddress - address;
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }

    public int writeCode(List<String> lines, String label) {
        int size = armParser.getByteLength(lines);
        int romAddress = allocateRom(size);

        writeCodeInternal(lines, romAddress, label);
        return romAddress;
    }

    public int writeCodeUnnamed(List<String> lines) {
        int size = armParser.getByteLength(lines);
        int romAddress = allocateRom(size);

        String label = String.format("Code_0x%08X", romAddress);
        writeCodeInternal(lines, romAddress, label);
        return romAddress;
    }

    private void writeCodeInternal(List<String> lines, int romAddress, String label) {
        int ramAddress = romToRamAddress(romAddress);
        byte[] bytes = armParser.parse(lines, this, ramAddress);
        writeBytes(romAddress, bytes);

        globalAddressMap.registerCodeAddress(this, label, ramAddress, 2);
    }

    public void writeCodeForceInline(List<String> lines, String label) {
        int ramAddress = globalAddressMap.getRamAddress(this, label);
        int romAddress = ramToRomAddress(ramAddress);
        int oldSize = getFuncSizeRam(ramAddress);

        byte[] bytes = armParser.parse(lines, this, ramAddress);
        if (true || (bytes.length == oldSize)) {
            for (int i = 0; i < bytes.length; i += 2) {
                int address = romAddress + i;
                int oldValue = readUnsignedHalfword(address);
                int newValue = (bytes[i] & 0xFF) | ((bytes[i + 1] & 0xFF) << 8);
                if (oldValue != newValue) {
                    continue;
                }
            }
        }

        if (bytes.length > oldSize || label.equals("GetTrainerData")) {
            if (bytes.length <= 16)
                throw new RuntimeException();

            int newRomAddress = allocateRom(bytes.length);
            int newRamAddress = romToRamAddress(newRomAddress);
            bytes = armParser.parse(lines, this, newRamAddress);
            writeBytes(newRomAddress, bytes);
            globalAddressMap.relocateCodeAddress(this, label, newRamAddress);

            List<String> redirectorLines = Arrays.asList(
                    "bx pc",
                    "dcw 0x46C0", // nop
                    "dcd 0xE59FC000", // ldr r12, =(newRomAddress+1)
                    "dcd 0xE12FFF1C", // bx r12
                    "dcd " + (newRamAddress + 1)
            );
            bytes = armParser.parse(redirectorLines, this, ramAddress);
            writeBytes(romAddress, bytes);
            free(romAddress + bytes.length, oldSize - bytes.length);
            return;
        }

        Map<Integer, Set<Integer>> oldOutgoingReferencesMap = armParser.getOutgoingCodeReferences(this, ramAddress);
        writeBytes(romAddress, bytes);
        Map<Integer, Set<Integer>> newOutgoingReferencesMap = armParser.getOutgoingCodeReferences(this, ramAddress);

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
                List<Integer> sourceAddresses = entry.getValue().stream().toList();
                for (int sourceAddress : sourceAddresses) {
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

        int diff = oldSize - bytes.length;
        free(romAddress + bytes.length, diff);
    }

    public void replaceCode(List<String> lines, String label) {
        int size = armParser.getByteLength(lines);
        int romAddress = allocateRom(size);

        int ramAddress = romToRamAddress(romAddress);
        byte[] bytes = armParser.parse(lines, this, ramAddress);
        writeBytes(romAddress, bytes);

        globalAddressMap.relocateCodeAddress(this, label, ramAddress);
    }

    public int writeData(byte[] bytes, String label) {
        return writeData(bytes, label, null);
    }

    public int writeData(byte[] bytes, String label, String refPattern) {
        int romAddress = allocateRom(bytes.length);
        writeDataInternal(bytes, label, romAddress, refPattern);
        return romAddress;
    }

    public int writeDataUnnamed(byte[] bytes, String refPattern) {
        int romAddress = allocateRom(bytes.length);
        String label = String.format("Data_0x%08X", romAddress);

        writeDataInternal(bytes, label, romAddress, refPattern);
        return romAddress;
    }

    private void writeDataInternal(byte[] bytes, String label, int romAddress, String refPattern) {
        writeBytes(romAddress, bytes);
        int ramAddress = romToRamAddress(romAddress);
        globalAddressMap.registerDataAddress(this, label, ramAddress, bytes.length, refPattern);
    }
    
    public void replaceData(byte[] bytes, String label) {
        replaceData(bytes, label, null);
    }

    public void replaceData(byte[] bytes, String label, String refPattern) {
        int romAddress = allocateRom(bytes.length);
        int ramAddress = romToRamAddress(romAddress);
        writeBytes(romAddress, bytes);
        
        globalAddressMap.relocateDataAddress(this, label, ramAddress, bytes.length, refPattern);
    }

    public int newData(byte[] bytes, String label, String refPattern) {
        int romAddress = allocateRom(bytes.length);
        writeBytes(romAddress, bytes);

        int ramAddress = romToRamAddress(romAddress);
        globalAddressMap.relocateDataAddress(this, label, ramAddress, bytes.length, refPattern);
        return romAddress;
    }

    public static boolean isAligned(int address, int bytes) {
        return align(address, bytes) == address;
    }

    public static int align(int address, int bytes) {
        return address & -bytes;
    }

    public static int alignNext(int address, int bytes) {
        return align(address + bytes - 1, bytes);
    }

    @Override
    public String toString() {
        return name;
    }
}
