package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_586_Boomburst extends MoveHackMod {
    public MoveHackMod_586_Boomburst() {
        super(Moves.boomburst);
    }

    @Override
    public boolean addAnimation() {
        return true;
    }

    @Override
    public int[] getAnimationSpaFiles() {
        return new int[]{752};
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return false;
    }
}
