package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_091_Dig extends MoveHackMod {
    public MoveHackMod_091_Dig() {
        super(Moves.dig);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
    }

    @Override
    public Boolean isAssistUncallableMove() {
        return true;
    }
}
