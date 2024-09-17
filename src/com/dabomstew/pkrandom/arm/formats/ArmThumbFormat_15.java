package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.*;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_RegisterList;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_15 extends ArmThumbFormat {
    static class Line extends ArmInstructionLine {
        public Line(String opcode, ArmArg_Register rb, ArmArg_RegisterList rlist) {
            super(opcode, rb, rlist);
        }

        @Override
        public String getArgsString(ArmContext context) {
            return String.format("%s!, %s", args[0], args[1]);
        }
    }
    
    public static final Identifier IDENTIFIER = new Identifier(0xC000, 0xF000);

    private enum Opcode {
        stm,
        ldm
    }
    
    @Override
    public String getName() {
        return "Multiple load/store";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 11, 1);
        Opcode opcode = Opcode.values()[opcodeValue];
        
        int rbValue = readInstructionSegment(instruction, 8, 3);
        ArmArg_Register rb = new ArmArg_Register(rbValue);
        
        int rlistBits = readInstructionSegment(instruction, 0, 8);
        ArmArg_RegisterList rlist = new ArmArg_RegisterList(rlistBits);
        
        return new Line(opcode.toString(), rb, rlist);
    }
}
