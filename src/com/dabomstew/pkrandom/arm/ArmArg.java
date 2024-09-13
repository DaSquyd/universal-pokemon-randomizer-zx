package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

import java.util.Comparator;

public abstract class ArmArg implements Comparable<ArmArg> {
    protected int value;
    
    public abstract String getName();
    public abstract String toString();
    
    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(ArmArg o) {
        return Integer.compare(value, o.value);
    }
    
    public static class Comparator implements java.util.Comparator<ArmArg> {
        @Override
        public int compare(ArmArg o1, ArmArg o2) {
            return o1.compareTo(o2);
        }
    }
}
