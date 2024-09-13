package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Immediate;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_ImmediateHex;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_12 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0xA000, 0xF000);
    
    @Override
    public String getName() {
        return "Load address";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int rsValue = readInstructionSegment(instruction, 11, 1);
        ArmArg_Register rs = new ArmArg_Register(rsValue == 0 ? 15 : 13);

        int rdValue = readInstructionSegment(instruction, 8, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);
        
        int word8Value = readInstructionSegment(instruction, 0, 8);
        ArmArg_Immediate word8 = new ArmArg_ImmediateHex(word8Value, 8, 2);
        
        return new ArmInstructionLine("add", rd, rs, word8);
    }
}
