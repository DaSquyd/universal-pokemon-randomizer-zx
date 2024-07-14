package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.RomFunctions;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.constants.Gen5Constants;

import java.io.IOException;
import java.util.*;

public class ParagonLiteArm9 extends ParagonLiteOverlay {
    // The actual arm9 ptr in the ROM handler
    byte[] arm9;

    public ParagonLiteArm9(byte[] arm9, ParagonLiteAddressMap addressMap) {
        super(-1, "ARM9", Arrays.copyOf(arm9, arm9.length), Gen5Constants.arm9Offset, Insertion.Front, addressMap);
        
        this.arm9 = arm9;
    }

    @Override
    public void save(Gen5RomHandler romHandler) {
        System.arraycopy(data, 0, arm9, 0, size());
    }

    @Override
    public int allocateExtend(int size) {
        throw new RuntimeException("Cannot extend ARM9");
    }
}
