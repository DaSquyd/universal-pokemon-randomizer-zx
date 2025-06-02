package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_571_Spotlight extends MoveHackMod {
    public MoveHackMod_571_Spotlight() {
        super(Moves.spotlight);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
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
