package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Immediate;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidOpcodeException;

public class ArmThumbFormat_01 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0x0000, 0xE000);
    
    private enum Opcode {
        lsl,
        lsr,
        asr
    }
    
    @Override
    public String getName() {
        return "Move shifted register";
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
        ArmArg_Immediate offset5 = new ArmArg_Immediate(offset5Value, 5, 0);

        int rsValue = readInstructionSegment(instruction, 3, 3);
        ArmArg_LowRegister rs = new ArmArg_LowRegister(rsValue);
        
        int rdValue = readInstructionSegment(instruction, 0, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);
        
        return new ArmInstructionLine(opcode.toString(), rd, rs, offset5);
    }
    
    private boolean isValidOpCode(int opcode) {
        return opcode >= 0 && opcode < Opcode.values().length;
    }
}
