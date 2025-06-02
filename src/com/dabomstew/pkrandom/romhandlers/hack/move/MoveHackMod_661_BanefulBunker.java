package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_661_BanefulBunker extends MoveHackMod {
    public MoveHackMod_661_BanefulBunker() {
        super(Moves.banefulBunker);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isProtectionMove() {
        return true;
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
