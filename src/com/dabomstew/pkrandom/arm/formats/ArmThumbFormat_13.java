package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Immediate;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_ImmediateHex;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_13 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0xB000, 0xFF00);

    private enum Opcode {
        add,
        sub
    }
    
    @Override
    public String getName() {
        return "Add offset to Stack Pointer";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 7, 1);
        Opcode opcode = Opcode.values()[opcodeValue];
        
        int word8Value = readInstructionSegment(instruction, 0, 7);
        ArmArg_Immediate word8 = new ArmArg_ImmediateHex(word8Value, 7, 2);
        
        return new ArmInstructionLine(opcode.toString(), new ArmArg_Register(ArmArg_Register.Type.sp), word8);
    }
}
