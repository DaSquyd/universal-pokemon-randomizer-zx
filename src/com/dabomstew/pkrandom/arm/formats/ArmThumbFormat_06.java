package com.dabomstew.pkrandom.arm.formats;

import com.dabomstew.pkrandom.arm.*;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Immediate;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_ImmediateHex;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_LowRegister;
import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmThumbFormat_06 extends ArmThumbFormat {
    static class Line extends ArmInstructionLine_Load {
        public Line(ArmArg_Register rd, ArmArg_Immediate word8) throws ArmDecodeException {
            super("ldr", rd, new ArmArg_Register(15), word8);
        }

        ArmArg_Register getRegisterArg() {
            return (ArmArg_Register) args[0];
        }

        int getWord8() {
            return args[2].getValue();
        }

        @Override
        public String getArgsString(ArmContext context) throws ArmDecodeException {
            int dataAddress = context.getCurrentAddress() + getWord8() + 4;
            int dataValue = context.getDataAtAddress(dataAddress);

            int hexDigits;
            if (dataValue <= 0xFF)
                hexDigits = 2;
            else if (dataValue <= 0xFFFF)
                hexDigits = 4;
            else
                hexDigits = 8;

            String format = String.format("%%s, =0x%%0%dX", hexDigits);
            return String.format(format, args[0], dataValue);
        }
    }

    public static final Identifier IDENTIFIER = new Identifier(0x4800, 0xF800);

    @Override
    public String getName() {
        return "PC-relative load";
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ArmLine decode(int instruction) throws ArmDecodeException {
        int rdValue = readInstructionSegment(instruction, 8, 3);
        ArmArg_LowRegister rd = new ArmArg_LowRegister(rdValue);

        int word8Value = readInstructionSegment(instruction, 0, 8);
        ArmArg_Immediate word8 = new ArmArg_ImmediateHex(word8Value, 8, 2);

        return new Line(rd, word8);
    }
}
