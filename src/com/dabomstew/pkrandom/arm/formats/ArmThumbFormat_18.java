package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine_Branch;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_SignedImmediate;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_18 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0xE000, 0xF800);

    @Override
    public String getName() {
        return "Unconditional branch";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int offset11Value = readInstructionSegment(instruction, 0, 11);
        ArmArg_SignedImmediate offset11 = new ArmArg_SignedImmediate(offset11Value, 11, 1);

        return new ArmInstructionLine_Branch("b", offset11);
    }
}
