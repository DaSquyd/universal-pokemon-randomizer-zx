package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.arm.argtypes.ArmArg_SignedImmediate;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmInstructionLine_Branch extends ArmInstructionLine {
    public ArmInstructionLine_Branch(String opcode, ArmArg_SignedImmediate imm) {
        super(opcode, imm);
    }

    @Override
    public void updateContext(ArmContext context) throws ArmDecodeException {
        int labelAddress = getLabelAddress(context);
        context.addLabelAddress(labelAddress);
    }

    @Override
    public String getArgsString(ArmContext context) throws ArmDecodeException {
        int labelAddress = getLabelAddress(context);
        return String.format("Label_0x%08X", labelAddress);
    }

    private int getLabelAddress(ArmContext context) throws ArmDecodeException {
        if (!(args[0] instanceof ArmArg_SignedImmediate imm))
            throw new ArmDecodeException();

        int currentAddress = context.getCurrentAddress();
        return currentAddress + imm.getValue() + 4;
    }
}
