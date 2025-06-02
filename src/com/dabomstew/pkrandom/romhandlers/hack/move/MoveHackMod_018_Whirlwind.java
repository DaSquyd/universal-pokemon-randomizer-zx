package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_018_Whirlwind extends MoveHackMod {
    public MoveHackMod_018_Whirlwind() {
        super(Moves.whirlwind);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
    }

    @Override
    public Boolean isAssistUncallableMove() {
        return true;
    }

    @Override
    public Boolean isCopycatUncallableMove() {
        return true;
    }
}
