package com.dabomstew.pkrandom.romhandlers.hack.move.custom;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_013_RazorWind_SingleTurn extends MoveHackMod {
    public MoveHackMod_013_RazorWind_SingleTurn() {
        super(Moves.razorWind);
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
