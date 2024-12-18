package com.dabomstew.pkrandom.arm.exceptions;

public class ArmParseEvalException extends ArmParseException {
    @java.io.Serial
    private static final long serialVersionUID = 0xF25CD07739031132L;

    public ArmParseEvalException() {
        super();
    }

    public ArmParseEvalException(int line, String lineText, String eval) {
        super(line, lineText, String.format("Unable to evaluate \"%s\"", eval));
    }
}
