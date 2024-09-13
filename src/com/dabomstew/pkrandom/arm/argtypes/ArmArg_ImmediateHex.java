package com.dabomstew.pkrandom.arm.argtypes;

import com.dabomstew.pkrandom.arm.ArmArg;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidValueRangeException;

public class ArmArg_ImmediateHex extends ArmArg_Immediate {
    int hexDigits;
    
    public ArmArg_ImmediateHex(int value, int bitSize) throws ArmDecodeException {
        super(value, bitSize);
        this.hexDigits = 0;
    }
    
    public ArmArg_ImmediateHex(int value, int bitSize, int bitShift) throws ArmDecodeException {
        super(value, bitSize, bitShift);
        this.hexDigits = 0;
    }
    
    public ArmArg_ImmediateHex(int value, int bitSize, int bitShift, int hexDigits) throws ArmDecodeException {
        super(value, bitSize, bitShift);
        this.hexDigits = hexDigits;
    }

    @Override
    public String toString() {        
        if (hexDigits <= 0) {
            if (getValue() <= 0xFF)
                hexDigits = 2;
            else if (getValue() <= 0xFFFF)
                hexDigits = 4;
            else
                hexDigits = 8;
        }

        String format = String.format("#0x%%0%dX", hexDigits);
        return String.format(format, getValue());
    }
}
