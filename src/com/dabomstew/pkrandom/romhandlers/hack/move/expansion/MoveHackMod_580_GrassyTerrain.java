package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_580_GrassyTerrain extends MoveHackMod {
    public MoveHackMod_580_GrassyTerrain() {
        super(Moves.grassyTerrain);
    }

    @Override
    public boolean addAnimation() {
        return true;
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
        
        return true;
    }
}
