package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_583_PlayRough extends MoveHackMod {
    public MoveHackMod_583_PlayRough() {
        super(Moves.playRough);
    }

    @Override
    public boolean addAnimation() {
        return true;
    }

    @Override
    public int[] getAnimationSpaFiles() {
        return new int[]{740};
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
        
        return true;
    }
}
