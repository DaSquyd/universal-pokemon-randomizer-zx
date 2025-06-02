package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_669_SolarBlade extends MoveHackMod {
    public MoveHackMod_669_SolarBlade() {
        super(Moves.solarBlade);
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
