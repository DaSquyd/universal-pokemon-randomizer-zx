package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_267_NaturePower extends MoveHackMod {
    public MoveHackMod_267_NaturePower() {
        super(Moves.naturePower);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
    }

    @Override
    public Boolean isEncoreFailMove() {
        return true;
    }
}
