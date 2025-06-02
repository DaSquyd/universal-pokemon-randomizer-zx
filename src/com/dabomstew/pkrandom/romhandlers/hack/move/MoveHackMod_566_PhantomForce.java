package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_566_PhantomForce extends MoveHackMod {
    public MoveHackMod_566_PhantomForce() {
        super(Moves.phantomForce);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isSleepTalkUncallableMove() {
        return true;
    }

    @Override
    public Boolean isAssistUncallableMove() {
        return true;
    }
}
