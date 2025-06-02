package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_690_BeakBlast extends MoveHackMod {
    public MoveHackMod_690_BeakBlast() {
        super(Moves.beakBlast);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isMeFirstFailMove() {
        return true;
    }

    @Override
    public Boolean isUncallableMove() {
        return true;
    }
}
