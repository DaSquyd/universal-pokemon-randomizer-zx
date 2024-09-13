package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine_Load;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_ImmediateHex;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidOpcodeException;

public class ArmThumbFormat_09 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0x6000, 0xE000);

    private enum Opcode {
        str,
        ldr,
        strb,
        ldrb
    }

    @Override
    public String getName() {
        return "Load/store with immediate offset";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 11, 2);
        if (!isValidOpCode(opcodeValue))
            throw new ArmInvalidOpcodeException(opcodeValue);
        Opcode opcode = Opcode.values()[opcodeValue];

        int offset5Value = readInstructionSegment(instruction, 6, 5);
        ArmArg_ImmediateHex offset5;
        switch (opcode) {
            case str, ldr -> offset5 = new ArmArg_ImmediateHex(offset5Value, 5, 2);
            case strb, ldrb -> offset5 = new ArmArg_ImmediateHex(offset5Value, 5);
            default -> throw new ArmDecodeException("Unexpected value: " + opcode);
        }

        int rbValue = readInstructionSegment(instruction, 3, 3);
        ArmArg_LowRegister rb = new ArmArg_LowRegister(rbValue);

        int rdValue = readInstructionSegment(instruction, 0, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);

        return new ArmInstructionLine_Load(opcode.toString(), rd, rb, offset5);
    }

    private boolean isValidOpCode(int opcode) {
        return opcode >= 0 && opcode < Opcode.values().length;
    }
}
