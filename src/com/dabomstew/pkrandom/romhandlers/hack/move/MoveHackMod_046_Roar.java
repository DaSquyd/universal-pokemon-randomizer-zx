package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_046_Roar extends MoveHackMod {
    public MoveHackMod_046_Roar() {
        super(Moves.roar);
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
