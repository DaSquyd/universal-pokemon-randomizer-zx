package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_ImmediateHex;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_17 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0xDF00, 0xFF00);

    @Override
    public String getName() {
        return "Software interrupt";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int value8Value = readInstructionSegment(instruction, 0, 8);
        ArmArg_ImmediateHex value8 = new ArmArg_ImmediateHex(value8Value, 8);

        return new ArmInstructionLine("swi", value8);
    }
}
