package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public class ArmDataLine extends ArmLine {
    public int value;

    public ArmDataLine(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        int length;
        if (value < 0xFF)
            length = 2;
        else if (value < 0xFFFF)
            length = 4;
        else
            length = 8;
        String format = String.format("%%-8s0x%%0%dX", length);
        return String.format(format, "dcd", value);
    }

    @Override
    public void updateContext(ArmContext context) {
    }

    @Override
    public final String toString(ArmContext context) {
        return toString();
    }
}
