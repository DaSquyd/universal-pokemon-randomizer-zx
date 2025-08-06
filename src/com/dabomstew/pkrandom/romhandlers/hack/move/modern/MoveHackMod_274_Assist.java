package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_274_Assist extends MoveHackMod {
    public MoveHackMod_274_Assist() {
        super(Moves.assist);
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
