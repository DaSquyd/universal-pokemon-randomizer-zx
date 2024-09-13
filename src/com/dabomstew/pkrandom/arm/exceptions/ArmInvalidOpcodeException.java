package com.dabomstew.pkrandom.arm.exceptions;

public class ArmInvalidOpcodeException extends ArmDecodeException {
        @java.io.Serial
        private static final long serialVersionUID = 0xD04ED442EE766352L;
        
        public ArmInvalidOpcodeException(int opcode) {
            super(String.format("Invalid opcode %d", opcode));
        }
}
