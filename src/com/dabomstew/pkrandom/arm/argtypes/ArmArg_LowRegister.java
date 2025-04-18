package com.dabomstew.pkrandom.arm.argtypes;

import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmParseException;

public class ArmArg_LowRegister extends ArmArg_Register {

    public ArmArg_LowRegister(int line, String op, String[] args, int arg) throws ArmParseException {
        super(line, op, args, arg);
    }

    public ArmArg_LowRegister(int value) throws ArmDecodeException {
        super(value);    
    }

    @Override
    public String getName() {
        return "Low Register";
    }

    @Override
    protected int getMaxRegister() {
        return 7;
    }
}
