package com.dabomstew.pkrandom.arm.exceptions;

public class BranchOffsetException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0x87BD7D8B209C1193L;

    public BranchOffsetException() {
        super();
    }

    public BranchOffsetException(int line, String op, String[] args, int arg, int offset, int min, int max) {
        super(line, op, args, String.format("Branch offset of %d bytes to %s is too large; must be between %d and %d", offset, args[arg], min, max));
    }
}
