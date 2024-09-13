package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Immediate;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidOpcodeException;

public class ArmThumbFormat_03 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0x2000, 0xE000);
    
    private enum Opcode {
        mov,
        cmp,
        add,
        sub
    }
    
    @Override
    public String getName() {
        return "Move/compare/add/subtract immediate";
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
        
        int rdValue = readInstructionSegment(instruction, 8, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);
        
        int offset8Value = readInstructionSegment(instruction, 0, 8);
        ArmArg_Immediate offset8 = new ArmArg_Immediate(offset8Value, 8);
        
        return new ArmInstructionLine(opcode.toString(), rd, offset8);
    }
    
    private boolean isValidOpCode(int opcode) {
        return opcode >= 0 && opcode < Opcode.values().length;
    }
}
