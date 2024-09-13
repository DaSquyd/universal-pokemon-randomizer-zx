package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidOpcodeException;

public class ArmThumbFormat_05 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0x4400, 0xFC00);
    
    private enum Opcode {
        add,
        cmp,
        mov,
        bx
    }
    
    @Override
    public String getName() {
        return "Hi register operations/branch exchange";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 6, 4);
        if (!isValidOpCode(opcodeValue))
            throw new ArmInvalidOpcodeException(opcodeValue);
        Opcode opcode = Opcode.values()[opcodeValue];
        
        int rsValue = readInstructionSegment(instruction, 3, 4);
        ArmArg_Register rs = new ArmArg_Register(rsValue);
        
        if (opcode == Opcode.bx)
            return new ArmInstructionLine(opcode.toString(), rs);
        
        int hd = readInstructionSegment(instruction, 7, 1);
        int rdValue = readInstructionSegment(instruction, 0, 3);
        ArmArg_Register rd = new ArmArg_Register(rdValue | (hd << 3));
        
        return new ArmInstructionLine(opcode.toString(), rd, rs);
    }
    
    private boolean isValidOpCode(int opcode) {
        return opcode >= 0 && opcode < Opcode.values().length;
    }
}
