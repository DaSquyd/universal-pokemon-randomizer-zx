package com.dabomstew.pkrandom.arm.argtypes;

import com.dabomstew.pkrandom.arm.ArmArg;
import com.dabomstew.pkrandom.arm.exceptions.ArmArgException;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.arm.exceptions.ArmParseException;

public class ArmArg_Register extends ArmArg {
    public enum Type {
        r0,
        r1,
        r2,
        r3,
        r4,
        r5,
        r6,
        r7,
        r8,
        r9,
        r10,
        r11,
        r12,
        sp,
        lr,
        pc
    }

    public ArmArg_Register(int line, String arg) throws ArmParseException {
        value = parseArg(line, arg);
    }

    public ArmArg_Register(Type type) throws ArmDecodeException {
        if (type == null)
            throw new ArmDecodeException("Invalid register value null");
        
        this.value = type.ordinal();
        validate();
    }
    
    public ArmArg_Register(int value) throws ArmDecodeException {
        this.value = value;
        validate();
    }
    
    private void validate() throws ArmDecodeException {
        if (value < getMinRegister() || value > getMaxRegister())
            throw new ArmDecodeException(String.format("Invalid register value %d", value));
    }
    
    @Override
    public String getName() {
        return "Register";
    }

    @Override
    public String toString() {
        return switch (value) {
            case 0 -> "r0";
            case 1 -> "r1";
            case 2 -> "r2";
            case 3 -> "r3";
            case 4 -> "r4";
            case 5 -> "r5";
            case 6 -> "r6";
            case 7 -> "r7";
            case 8 -> "r8";
            case 9 -> "r9";
            case 10 -> "r10";
            case 11 -> "r11"; // frame pointer
            case 12 -> "r12"; // intra procedural call
            case 13 -> "sp"; // stack pointer
            case 14 -> "lr"; // link register
            case 15 -> "pc"; // program counter
            default -> "UNKNOWN REGISTER";
        };
    }
    
    protected int parseArg(int line, String arg) throws ArmArgException {
        return switch (arg.toLowerCase()) {
            case "r0" -> 0;
            case "r1" -> 1;
            case "r2" -> 2;
            case "r3" -> 3;
            case "r4" -> 4;
            case "r5" -> 5;
            case "r6" -> 6;
            case "r7" -> 7;
            case "r8" -> 8;
            case "r9" -> 9;
            case "r10" -> 10;
            case "r11", "fp" -> 11;
            case "r12", "ip" -> 12;
            case "r13", "sp" -> 13;
            case "r14", "lr" -> 14;
            case "r15", "pc" -> 15;
            default -> throw new ArmArgException(line, arg);
        };
    }

    protected int getMinRegister() {
        return 0;
    }
    
    protected int getMaxRegister() {
        return 15;
    }
}
