package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_383_CopyCat extends MoveHackMod {
    public MoveHackMod_383_CopyCat() {
        super(Moves.copycat);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return true;
    }

    @Override
    public Boolean isEncoreFailMove() {
        return true;
    }
}
