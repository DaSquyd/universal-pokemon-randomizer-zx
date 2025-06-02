package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_601_Geomancy extends MoveHackMod {
    public MoveHackMod_601_Geomancy() {
        super(Moves.geomancy);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isSleepTalkUncallableMove() {
        return true;
    }
}
