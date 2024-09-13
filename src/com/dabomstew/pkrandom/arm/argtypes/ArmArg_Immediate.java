package com.dabomstew.pkrandom.arm.argtypes;

import com.dabomstew.pkrandom.arm.ArmArg;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidValueRangeException;

public class ArmArg_Immediate extends ArmArg {
    protected int bitSize;
    protected int bitShift;

    public ArmArg_Immediate(int value, int bitSize) throws ArmDecodeException {
        this.value = value;
        this.bitSize = bitSize;
        this.bitShift = 0;
        Validate();
    }
    
    public ArmArg_Immediate(int value, int bitSize, int bitShift) throws ArmDecodeException {
        this.value = value;
        this.bitSize = bitSize;
        this.bitShift = bitShift;
        Validate();
    }
    
    private void Validate() throws ArmDecodeException {
        int maxValue = getMaxValue();
        if (value < 0 || value > maxValue)
            throw new ArmInvalidValueRangeException(value, 0, maxValue);
        
        if (bitShift < 0)
            throw new ArmDecodeException();
    }

    @Override
    public int getValue() {
        return value << bitShift;
    }

    @Override
    public String getName() {
        return "Immediate";
    }

    @Override
    public String toString() {
        return String.format("#%d", getValue());
    }

    int getMaxValue() {
        return (1 << bitSize) - 1;
    }
}
