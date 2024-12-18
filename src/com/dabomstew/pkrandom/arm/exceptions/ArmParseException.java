package com.dabomstew.pkrandom.arm.exceptions;

public class ArmParseException extends Exception {
    @java.io.Serial
    private static final long serialVersionUID = 0xA5BD1C1B37FDFED8L;

    public ArmParseException() {
        super();
    }
    
    public ArmParseException(int line, String lineText, String s) {

        super(String.format("Line %d; \"%s\" %s", line + 1, lineText, s));
    }
    
    public ArmParseException(int line, String op, String[] args, String s) {
        
        super(String.format("Line %d; \"%s %s\" %s", line + 1, op, String.join(", ", args), s));
    }
}
