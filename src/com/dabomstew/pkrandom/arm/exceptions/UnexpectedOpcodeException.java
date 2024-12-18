package com.dabomstew.pkrandom.arm.exceptions;

public class UnexpectedOpcodeException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0x22A9C619D216438EL;

    public UnexpectedOpcodeException() {
        super();
    }

    public UnexpectedOpcodeException(int line, String op, String[] args, int opcode, int minOpcode, int maxOpcode) {
        super(line, op, args, String.format("Unexpected opcode \"%d\"; expected %d-%d", opcode, minOpcode, maxOpcode));
    }
}
