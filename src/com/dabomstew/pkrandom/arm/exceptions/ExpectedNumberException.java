package com.dabomstew.pkrandom.arm.exceptions;

public class ExpectedNumberException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0xE3035C85A38B3F7EL;

    public ExpectedNumberException() {
        super();
    }
    
    public ExpectedNumberException(int line, String op, String[] args, int arg, int number, int min, int max) {
        super(line, op, args, String.format("Unexpected token \"%s\" (%d) for argument %d; must be between %d and %d, inclusive", args[arg], number, arg, min, max));
    }
}
