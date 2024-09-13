package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.*;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_SignedImmediate;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_19 extends ArmThumbFormat {
    public static class Line extends ArmInstructionLine {
        public Line(String opcode, ArmArg_SignedImmediate imm) {
            super(opcode, imm);
        }

        @Override
        public String getArgsString(ArmContext context) throws ArmDecodeException {
            if (!(args[0] instanceof ArmArg_SignedImmediate imm))
                throw new ArmDecodeException();

            int currentAddress = context.getCurrentAddress();
            int funcAddress = currentAddress + imm.getValue() + 4;
            return context.getFuncName(funcAddress);
        }
    }
    
    public static final Identifier IDENTIFIER = new Identifier(0xF000, 0xF800);

    @Override
    public String getName() {
        return "Long branch with link";
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int offsetLow = readInstructionSegment(instruction, 16, 11);
        int offsetHigh = readInstructionSegment(instruction, 0, 11);
        ArmArg_SignedImmediate offset22 = new ArmArg_SignedImmediate((offsetHigh << 11) | (offsetLow), 22, 1);

        return new Line("bl", offset22);
    }
}
