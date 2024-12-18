package com.dabomstew.pkrandom.arm.exceptions;

public class ExpectedOpException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0x9B3C0FB852717ED5L;

    public ExpectedOpException() {
        super();
    }

    public ExpectedOpException(int line, String op, String[] args, String expectedOp) {
        super(line, op, args, String.format("Unexpected op \"%s\"; expected %s", op, expectedOp));
    }

    public ExpectedOpException(int line, String op, String[] args, String... expectedOps) {
        super(line, op, args, String.format("Unexpected op \"%s\"; expected [%s]", op, String.join(", ", expectedOps)));
    }
}
