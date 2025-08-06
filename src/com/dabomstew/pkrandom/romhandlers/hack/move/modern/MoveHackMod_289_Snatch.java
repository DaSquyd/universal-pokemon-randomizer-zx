package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_289_Snatch extends MoveHackMod {
    public MoveHackMod_289_Snatch() {
        super(Moves.snatch);
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
