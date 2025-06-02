package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_291_Dive extends MoveHackMod {
    public MoveHackMod_291_Dive() {
        super(Moves.dive);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
    }

    @Override
    public Boolean isAssistUncallableMove() {
        return true;
    }
}
