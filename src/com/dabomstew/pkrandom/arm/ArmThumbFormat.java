package com.dabomstew.pkrandom.arm;

public abstract class ArmThumbFormat extends ArmFormat {
    @Override
    public abstract String getName();

    @Override
    public int getSize() {
        return 2;
    }
}
