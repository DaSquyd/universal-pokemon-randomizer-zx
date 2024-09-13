package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine_Load;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmInvalidOpcodeException;

public class ArmThumbFormat_07 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0x5000, 0xF200);

    private enum Opcode {
        str,
        strb,
        ldr,
        ldrb
    }
    
    @Override
    public String getName() {
        return "Load/store with register offset";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 10, 2);
        if (!isValidOpCode(opcodeValue))
            throw new ArmInvalidOpcodeException(opcodeValue);
        Opcode opcode = Opcode.values()[opcodeValue];
        
        int roValue = readInstructionSegment(instruction, 6, 3);
        ArmArg_LowRegister ro = new ArmArg_LowRegister(roValue);
        
        int rbValue = readInstructionSegment(instruction, 3, 3);
        ArmArg_LowRegister rb = new ArmArg_LowRegister(rbValue);
        
        int rdValue = readInstructionSegment(instruction, 0, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);
        
        return new ArmInstructionLine_Load(opcode.toString(), rd, rb, ro);
    }

    private boolean isValidOpCode(int opcode) {
        return opcode >= 0 && opcode < Opcode.values().length;
    }
}
