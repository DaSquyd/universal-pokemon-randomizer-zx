package com.dabomstew.pkrandom.arm.exceptions;

public class ExpectedRegisterException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0x7D6B24B1A41AE003L;

    public ExpectedRegisterException() {
        super();
    }

    public ExpectedRegisterException(int line, String op, String[] args, int arg) {
        super(line, op, args, String.format("Unexpected token \"%s\" for argument %d; expected a register r0-r15", args[arg], arg));
    }

    public ExpectedRegisterException(int line, String op, String[] args, int arg, String[] expectedRegisters) {
        super(line, op, args, String.format("Unexpected token \"%s\" for argument %d; expected a register [%s]", args[arg], arg, String.join(", ", expectedRegisters)));
    }

    public ExpectedRegisterException(int line, String op, String[] args, int token, String tokenValue, String[] expectedRegisters) {
        super(line, op, args, String.format("Unexpected token \"%s\" for token %d; expected a register [%s]", tokenValue, token, String.join(", ", expectedRegisters)));
    }
}
