package com.dabomstew.pkrandom.arm.exceptions;

public class ArmArgException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0x43A4462C48021334L;

    public ArmArgException(int line, String op, String[] args, int arg) {
        super(line, op, args, String.format("Invalid arg %s", args[arg]));
    }
}
