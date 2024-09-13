package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine_Load;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Immediate;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_ImmediateHex;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidOpcodeException;

public class ArmThumbFormat_11 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0x9000, 0xF000);

    private enum Opcode {
        str,
        ldr,
    }
    
    @Override
    public String getName() {
        return "SP-relative load/store";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 11, 1);
        if (!isValidOpCode(opcodeValue))
            throw new ArmInvalidOpcodeException(opcodeValue);
        Opcode opcode = Opcode.values()[opcodeValue];

        int rdValue = readInstructionSegment(instruction, 8, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);
        
        int word8Value = readInstructionSegment(instruction, 0, 8);
        ArmArg_Immediate word8 = new ArmArg_ImmediateHex(word8Value, 8, 2);
        
        return new ArmInstructionLine_Load(opcode.toString(), rd, new ArmArg_Register(13), word8);
    }

    private boolean isValidOpCode(int opcode) {
        return opcode >= 0 && opcode < Opcode.values().length;
    }
}
