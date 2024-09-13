package com.dabomstew.pkrandom.arm.argtypes;

import com.dabomstew.pkrandom.arm.ArmArg;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;

import java.util.ArrayList;
import java.util.List;

public class ArmArg_RegisterList extends ArmArg {
    List<ArmArg_Register> list;

    public ArmArg_RegisterList(int bits) throws ArmDecodeException {
        list = new ArrayList<>();
        for (int bit = 0; bit < 8; bit++) {
            int mask = 1 << bit;
            if ((mask & bits) != 0)
                list.add(new ArmArg_Register(bit));
        }
    }

    @Override
    public String getName() {
        return "Rlist";
    }

    @Override
    public String toString() {
        List<String> segments = new ArrayList<>();
        ArmArg runStart = null;
        ArmArg runEnd = null;
        for (ArmArg arg : list) {
            if (runEnd != null) {
                int lastValue = runEnd.getValue();
                int currentValue = arg.getValue();
                if (lastValue != currentValue - 1) {
                    segments.add(makeRunStr(runStart, runEnd));
                    runStart = null;
                    runEnd = null;
                }
            }

            if (runStart == null) {
                runStart = arg;
            } else {
                runEnd = arg;
            }
        }

        if (runStart != null) {
            if (runEnd != null) {
                segments.add(makeRunStr(runStart, runEnd));
            } else {
                segments.add(runStart.toString());
            }
        }

        return String.format("{%s}", String.join(", ", segments));
    }

    private String makeRunStr(ArmArg start, ArmArg end) {
        return String.format("%s-%s", start, end);
    }
    
    public void add(ArmArg_Register r) {
        list.add(r);
    }
}
