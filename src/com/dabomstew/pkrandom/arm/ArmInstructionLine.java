package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

import java.util.List;

public class ArmInstructionLine extends ArmLine {
    public String operation;
    public ArmArg[] args;

    public ArmInstructionLine(String operation, ArmArg... args) {
        this.operation = operation;
        this.args = args;
    }

    public ArmInstructionLine(String operation, List<ArmArg> args) {
        this.operation = operation;
        this.args = new ArmArg[args.size()];
        args.toArray(this.args);
    }

    @Override
    public void updateContext(ArmContext context) throws ArmDecodeException {
    }

    @Override
    public final String toString(ArmContext context) {
        try {
            String argsStr = getArgsString(context);
            return String.format("%-8s%s", operation, argsStr);
        } catch (ArmDecodeException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getArgsString(ArmContext context) throws ArmDecodeException {
        String[] argStrs = new String[args.length];
        
        for (int i = 0; i < args.length; i++) {
            argStrs[i] = args[i].toString();
        }
        
        return String.join(", ", argStrs);        
    }
}
