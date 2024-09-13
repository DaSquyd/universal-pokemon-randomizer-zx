package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public abstract class ArmFormat implements Comparable<Integer> {
    public static class Identifier {
        public int mask;
        public int value;

        public Identifier(int value, int mask) {
            this.value = value;
            this.mask = mask;
            
            if ((value & mask) != value)
                throw new RuntimeException(String.format("ArmFormat Identifier value 0x%04X did not match mask 0x%04X", value, mask));
        }
    }

    public static class Comparator implements java.util.Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null)
                o1 = -1;
            if (o2 == null)
                o2 = -1;
            
            if (o1 instanceof Integer int1 && o2 instanceof Integer int2)
                return Integer.compare(int1, int2);

            if (o1 instanceof ArmFormat format1 && o2 instanceof ArmFormat format2)
                return Integer.compare(format1.getIdentifier().value, format2.getIdentifier().value);

            if (o1 instanceof ArmFormat format && o2 instanceof Integer intValue) {
                Identifier identifier = format.getIdentifier();
                return Integer.compare(identifier.value, intValue & identifier.mask);
            }

            if (o1 instanceof Integer intValue && o2 instanceof ArmFormat format) {
                Identifier identifier = format.getIdentifier();
                return Integer.compare(intValue & identifier.mask, identifier.value);
            }

            throw new RuntimeException(String.format("Failed to compare %s and %s", o1, o2));
        }
    }

    public abstract String getName();

    public abstract Identifier getIdentifier();

    public abstract int getSize();

    public abstract ArmLine decode(int instruction) throws ArmDecodeException;

    protected int readInstructionSegment(int instruction, int offset, int length) {
        instruction >>>= offset;
        int mask = (1 << length) - 1;
        return (instruction & mask);
    }

    @Override
    public int compareTo(Integer o) {
        Identifier identifier = getIdentifier();
        return Integer.compare(identifier.value, o & identifier.mask);
    }
}
