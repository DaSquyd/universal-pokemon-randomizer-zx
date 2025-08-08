package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_584_FairyWind extends MoveHackMod {
    public MoveHackMod_584_FairyWind() {
        super(Moves.fairyWind);
    }

    @Override
    public boolean addAnimation() {
        return true;
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return false;
    }
}
