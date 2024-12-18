package com.dabomstew.pkrandom.arm.exceptions;

public class ArmParseArgCountException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0x19B595A537BA0A2EL;

    public ArmParseArgCountException() {
        super();
    }

    public ArmParseArgCountException(int line, String op, String[] args, int expectedCount) {
        super(line, op, args, String.format("Invalid argument count %d; must be %d", args.length, expectedCount));
    }

    public ArmParseArgCountException(int line, String op, String[] args, int min, int max) {
        super(line, op, args, String.format("Invalid argument count %d; must be [%d, %d]", args.length, min, max));
    }
}
