package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_572_PetalBlizzard extends MoveHackMod {
    public MoveHackMod_572_PetalBlizzard() {
        super(Moves.petalBlizzard);
    }

    @Override
    public boolean addAnimation() {
        return true;
    }

    @Override
    public int[] getAnimationSpaFiles() {
        return new int[]{786};
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return false;
    }
}
