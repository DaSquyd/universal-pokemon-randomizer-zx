package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.constants.Gen5Constants;

import java.util.*;

public class ParagonLiteArm9 extends ParagonLiteOverlay {
    public ParagonLiteArm9(Gen5RomHandler romHandler, byte[] arm9, ParagonLiteAddressMap addressMap) {
        super(romHandler, -1, "ARM9", Arrays.copyOf(arm9, arm9.length), Gen5Constants.arm9Offset, Insertion.Front, addressMap);
    }

    private int getItcmSrcAddress() {
        return switch (romHandler.getGen5GameIndex()) {
            case 0, 1 -> 0x020A9EA0; // W, B
            case 2 -> 0x0209D780; // W2
            case 3 -> 0x0209D740; // B2
            default -> throw new RuntimeException();
        };
    }

    private int getItcmDestStart() {
        return 0x01FF8000;
    }
    
    @Override
    public int romToRamAddress(int romAddress) {
        int itcmSrcAddress = getItcmSrcAddress();
        if (romAddress < itcmSrcAddress)
            return romAddress;

        return romAddress - itcmSrcAddress + getItcmDestStart();
    }

    @Override
    public int ramToRomAddress(int ramAddress) {
        if (ramAddress >= address)
            return ramAddress;

        return ramAddress - getItcmDestStart() + getItcmSrcAddress();
    }

    @Override
    public void save(Gen5RomHandler romHandler) {
        romHandler.replaceArm9(data);
    }

    @Override
    public int allocateExtend(int size) {
        int tcmCopyingPointersAddress = globalAddressMap.getRamAddress(this, "Data_StartModuleParams");

        int oldDestPointersAddress = readWord(tcmCopyingPointersAddress);
        int itcmSrcAddress = readWord(tcmCopyingPointersAddress + 8);
        int oldITCMSizeAddress = oldDestPointersAddress + 4;
        int oldITCMSize = readWord(oldITCMSizeAddress);

        int oldDTCMAddress = itcmSrcAddress + oldITCMSize;
        int newDTCMAddress = alignNext(oldDTCMAddress + size, 32);
        int extensionSize = newDTCMAddress - oldDTCMAddress;

        int newITCMSizeAddress = oldITCMSizeAddress + extensionSize;
        int newITCMSize = oldITCMSize + extensionSize;

        byte[] oldData = data;
        data = Arrays.copyOf(data, data.length + extensionSize);

        writeWord(tcmCopyingPointersAddress, oldDestPointersAddress + extensionSize, false);
        writeWord(tcmCopyingPointersAddress + 4, address + data.length, false);

        int oldDTCMOffset = oldDTCMAddress - address;
        int newDTCMOffset = newDTCMAddress - address;
        int dtcmSize = oldData.length - oldDTCMOffset;

        System.arraycopy(oldData, oldDTCMOffset, data, newDTCMOffset, dtcmSize);
        for (int i = oldDTCMOffset; i < newDTCMOffset; ++i) {
            data[i] = 0x00;
        }
        writeWord(newITCMSizeAddress, newITCMSize, false);

        FreeSpace lastFreeSpace = freeSpaces.isEmpty() ? null : freeSpaces.getLast();
        if (lastFreeSpace != null && lastFreeSpace.end >= oldDTCMAddress) {
            lastFreeSpace.end = newDTCMAddress;
        } else {
            freeSpaces.add(new FreeSpace(oldDTCMAddress, newDTCMAddress));
        }

        lastFreeSpace = freeSpaces.getLast();
        int allocationStart = lastFreeSpace.start;

        lastFreeSpace.start += size;
        if (lastFreeSpace.start == lastFreeSpace.end)
            freeSpaces.removeLast();
        else if (lastFreeSpace.start > lastFreeSpace.end)
            throw new RuntimeException();

        return allocationStart;
    }
}
