package com.dabomstew.pkrandom.arm.argtypes;

import com.dabomstew.pkrandom.arm.exceptions.ArmParseException;

public class ArmArg_HighRegister extends ArmArg_Register {

    ArmArg_HighRegister(String arg) throws ArmParseException {
        super(arg);
    }

    @Override
    public String getName() {
        return "High Register";
    }

    @Override
    protected int getMinRegister() {
        return 8;
    }
}
