package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_469_WideGuard extends MoveHackMod {
    public MoveHackMod_469_WideGuard() {
        super(Moves.wideGuard);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
        return true;
    }

    @Override
    public Boolean isProtectionMove() {
        return true;
    }
}
