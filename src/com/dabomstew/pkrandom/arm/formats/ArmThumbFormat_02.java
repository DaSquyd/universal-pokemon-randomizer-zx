package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Immediate;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidOpcodeException;

public class ArmThumbFormat_02 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0x1800, 0xF800);
    
    private enum Opcode {
        add,
        sub
    }
    
    @Override
    public String getName() {
        return "Add/subtract";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        boolean immFlag = readInstructionSegment(instruction, 10, 1) != 0;
        
        int opcodeValue = readInstructionSegment(instruction, 9, 1);
        if (!isValidOpCode(opcodeValue))
            throw new ArmInvalidOpcodeException(opcodeValue);
        Opcode opcode = Opcode.values()[opcodeValue];
        
        int rsValue = readInstructionSegment(instruction, 3, 3);
        ArmArg_LowRegister rs = new ArmArg_LowRegister(rsValue);
        
        int rdValue = readInstructionSegment(instruction, 0, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);
        
        if (immFlag)
            return decodeOffset3(instruction, opcode, rs, rd);
        return decodeRn(instruction, opcode, rs, rd);
    }

    private ArmLine decodeRn(int instruction, Opcode opcode, ArmArg_LowRegister rs, ArmArg_LowRegister rd) throws ArmDecodeException {
        int rnValue = readInstructionSegment(instruction, 6, 3);
        ArmArg_LowRegister rn = new ArmArg_LowRegister(rnValue);
        return new ArmInstructionLine(opcode.toString(), rd, rs, rn);
    }
    
    private ArmLine decodeOffset3(int instruction, Opcode opcode, ArmArg_LowRegister rs, ArmArg_LowRegister rd) throws ArmDecodeException {
        int offset5Value = readInstructionSegment(instruction, 6, 3);
        if (offset5Value == 0)
            return new ArmInstructionLine("mov", rd, rs);
        
        ArmArg_Immediate offset3 = new ArmArg_Immediate(offset5Value, 3, 0);
        return new ArmInstructionLine(opcode.toString(), rd, rs, offset3);
    }
    
    private boolean isValidOpCode(int opcode) {
        return opcode >= 0 && opcode < Opcode.values().length;
    }
}
