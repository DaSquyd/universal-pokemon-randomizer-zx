package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_782_BehemothBash extends MoveHackMod {
    public MoveHackMod_782_BehemothBash() {
        super(Moves.behemothBash);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isCopycatUncallableMove() {
        return true;
    }
}
