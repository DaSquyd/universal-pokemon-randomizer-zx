package com.dabomstew.pkrandom.arm.exceptions;

public class ImmediateWordAlignmentException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0xBFF002B669C526C4L;

    public ImmediateWordAlignmentException() {
        super();
    }

    public ImmediateWordAlignmentException(int line, String op, String[] args, int arg, int imm) {
        super(line, op, args, String.format("Expected immediate %d to be word-aligned for argument %d", imm, arg));
    }
}
