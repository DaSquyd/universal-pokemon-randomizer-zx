package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_578_CraftyShield extends MoveHackMod {
    public MoveHackMod_578_CraftyShield() {
        super(Moves.craftyShield);
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
