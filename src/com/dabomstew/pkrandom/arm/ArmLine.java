package com.dabomstew.pkrandom.arm;

import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

public abstract class ArmLine {
    public abstract void updateContext(ArmContext context) throws ArmDecodeException;
    public abstract String toString(ArmContext context) throws ArmDecodeException;
}
