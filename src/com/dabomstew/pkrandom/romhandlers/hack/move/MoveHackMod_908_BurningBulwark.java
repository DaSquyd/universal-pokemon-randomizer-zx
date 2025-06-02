package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_908_BurningBulwark extends MoveHackMod {
    public MoveHackMod_908_BurningBulwark() {
        super(Moves.burningBulwark);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isProtectionMove() {
        return true;
    }
}
