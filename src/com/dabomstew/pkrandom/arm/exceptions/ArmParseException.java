package com.dabomstew.pkrandom.arm.exceptions;

public class ArmParseException extends Exception {
    @java.io.Serial
    private static final long serialVersionUID = 0xA5BD1C1B37FDFED8L;

    public ArmParseException() {
        super();
    }
    public ArmParseException(int line, String s) {
        super(String.format("Line %d; %s", line + 1, s));
    }
}
