package com.dabomstew.pkrandom.arm.exceptions;

public class ArmInvalidValueRangeException extends ArmDecodeException {
        @java.io.Serial
        private static final long serialVersionUID = 0x14994EAFA70872F6L;
        
        public ArmInvalidValueRangeException(int value, int min, int max) {
            super(String.format("Invalid value range %d; must be [%d, %d]", value, min, max));
        }
}
