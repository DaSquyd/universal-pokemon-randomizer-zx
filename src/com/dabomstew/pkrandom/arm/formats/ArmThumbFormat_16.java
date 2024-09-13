package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine_Branch;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_SignedImmediate;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidOpcodeException;

public class ArmThumbFormat_16 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0xD000, 0xF000);

    private enum Opcode {
        beq,
        bne,
        bcs,
        bcc,
        bmi,
        bpl,
        bvs,
        bvc,
        bhi,
        bls,
        bge,
        blt,
        bgt,
        ble
    }

    @Override
    public String getName() {
        return "Conditional branch";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 8, 4);
        if (!isValidOpCode(opcodeValue))
            throw new ArmInvalidOpcodeException(opcodeValue);
        Opcode opcode = Opcode.values()[opcodeValue];

        int sOffset8Value = readInstructionSegment(instruction, 0, 8);
        ArmArg_SignedImmediate sOffset8 = new ArmArg_SignedImmediate(sOffset8Value, 8, 1);

        return new ArmInstructionLine_Branch(opcode.toString(), sOffset8);
    }

    private boolean isValidOpCode(int opcode) {
        return opcode >= 0 && opcode < Opcode.values().length;
    }
}
