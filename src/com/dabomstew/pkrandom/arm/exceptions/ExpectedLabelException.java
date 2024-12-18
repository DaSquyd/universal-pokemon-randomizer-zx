package com.dabomstew.pkrandom.arm.exceptions;

public class ExpectedLabelException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0x2CB7B8196E5EEF1DL;

    public ExpectedLabelException() {
        super();
    }

    public ExpectedLabelException(int line, String op, String[] args, int arg) {
        super(line, op, args, String.format("Unexpected token \"%s\" for argument %d; expected label", args[arg], arg));
    }

    public ExpectedLabelException(int line, String lineStr, String token) {
        super(line, lineStr, String.format("Unexpected token \"%s\"; expected label", token));
    }
}
