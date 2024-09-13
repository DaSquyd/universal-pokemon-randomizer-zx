package com.dabomstew.pkrandom.arm.argtypes;

import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmArg_SignedImmediate extends ArmArg_Immediate {
    public ArmArg_SignedImmediate(int value, int bitSize) throws ArmDecodeException {
        super(value, bitSize);
    }
    
    public ArmArg_SignedImmediate(int value, int bitSize, int bitShift) throws ArmDecodeException {
        super(value, bitSize, bitShift);
    }

    @Override
    public int getValue() {
        int displaySize = bitSize + bitShift;
        int maxShift = 32 - displaySize;

        int displayValue = value << bitShift;
        displayValue <<= maxShift;
        return displayValue >> maxShift;
    }
}
