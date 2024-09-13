package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.arm.argtypes.ArmArg_Register;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmInstructionLine_Load extends ArmInstructionLine {

    public ArmInstructionLine_Load(String operation, ArmArg_Register rd, ArmArg_Register rb, ArmArg imm) {
        super(operation, rd, rb, imm);
    }

    public ArmInstructionLine_Load(String operation, ArmArg_Register rd, ArmArg_Register rb, ArmArg_Register ro) {
        super(operation, rd, rb, ro);
    }

    @Override
    public String getArgsString(ArmContext context) throws ArmDecodeException {
        return String.format("%s, [%s, %s]", args[0], args[1], args[2]);
    }
}
