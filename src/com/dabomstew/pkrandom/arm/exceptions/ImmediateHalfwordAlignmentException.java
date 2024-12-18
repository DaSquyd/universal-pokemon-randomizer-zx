package com.dabomstew.pkrandom.arm.exceptions;

public class ImmediateHalfwordAlignmentException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0xEC1E8399CC492509L;

    public ImmediateHalfwordAlignmentException() {
        super();
    }

    public ImmediateHalfwordAlignmentException(int line, String op, String[] args, int arg, int imm) {
        super(line, op, args, String.format("Expected immediate %d to be halfword-aligned for argument %d", imm, arg));
    }
}
