package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_781_BehemothBlade extends MoveHackMod {
    public MoveHackMod_781_BehemothBlade() {
        super(Moves.behemothBlade);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }

    @Override
    public Boolean isCopycatUncallableMove() {
        return true;
    }
}
