package com.dabomstew.pkrandom.arm.exceptions;

public class ExpectedLowRegisterException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0x618041222FD76E89L;

    public ExpectedLowRegisterException() {
        super();
    }

    public ExpectedLowRegisterException(int line, String op, String[] args, int arg) {
        super(line, op, args, String.format("Unexpected token \"%s\" for argument %d; expected a register r0-r7", args[arg], arg));
    }

    public ExpectedLowRegisterException(int line, String op, String[] args, int token, String tokenValue) {
        super(line, op, args, String.format("Unexpected token \"%s\" for token %d; expected a register r0-r7", tokenValue, token));
    }
}
