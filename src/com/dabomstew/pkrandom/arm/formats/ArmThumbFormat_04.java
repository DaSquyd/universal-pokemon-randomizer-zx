package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.ArmLine;
import com.dabomstew.pkrandom.arm.ArmInstructionLine;
import com.dabomstew.pkrandom.arm.ArmThumbFormat;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_04 extends ArmThumbFormat {
    public static final Identifier IDENTIFIER = new Identifier(0x4000, 0xFC00);
    
    private enum Opcode {
        and,
        eor,
        lsl,
        lsr,
        asr,
        adc,
        sbc,
        ror,
        tst,
        neg,
        cmp,
        cmn,
        orr,
        mul,
        bic,
        mvn
    }
    
    @Override
    public String getName() {
        return "ALU operations";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int opcodeValue = readInstructionSegment(instruction, 6, 4);
        Opcode opcode = Opcode.values()[opcodeValue];
        
        int rsValue = readInstructionSegment(instruction, 3, 3);
        ArmArg_LowRegister rs = new ArmArg_LowRegister(rsValue);
        
        int rdValue = readInstructionSegment(instruction, 0, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);
        
        return new ArmInstructionLine(opcode.toString(), rd, rs);
    }
}
