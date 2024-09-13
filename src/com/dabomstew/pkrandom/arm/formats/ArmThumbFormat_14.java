package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.*;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_RegisterList;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_14 extends ArmThumbFormat {    
    public static final Identifier IDENTIFIER = new Identifier(0xB400, 0xF600);

    private enum Opcode {
        push,
        pop
    }
    
    @Override
    public String getName() {
        return "Push/pop registers";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 11, 1);
        Opcode opcode = Opcode.values()[opcodeValue];
        
        int rlistBits = readInstructionSegment(instruction, 0, 8);
        ArmArg_RegisterList rlist = new ArmArg_RegisterList(rlistBits);
        
        if (readInstructionSegment(instruction, 8, 1) != 0) {
            ArmArg_Register.Type pclrType = opcode == Opcode.push ? ArmArg_Register.Type.lr : ArmArg_Register.Type.pc;
            rlist.add(new ArmArg_Register(pclrType));
        }
        
        return new ArmInstructionLine(opcode.toString(), rlist);
    }
}
