package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_484_Heavy_Slam extends MoveHackMod {
    public MoveHackMod_484_Heavy_Slam() {
        super(Moves.heavySlam);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return true;
    }
}
