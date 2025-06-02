package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_562_Belch extends MoveHackMod {
    public MoveHackMod_562_Belch() {
        super(Moves.belch);
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
    public Boolean isSleepTalkUncallableMove() {
        return true;
    }

    @Override
    public Boolean isAssistUncallableMove() {
        return true;
    }
}
