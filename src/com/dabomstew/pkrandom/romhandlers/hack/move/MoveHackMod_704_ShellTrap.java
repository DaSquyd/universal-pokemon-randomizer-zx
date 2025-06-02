package com.dabomstew.pkrandom.romhandlers.hack.move;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_704_ShellTrap extends MoveHackMod {
    public MoveHackMod_704_ShellTrap() {
        super(Moves.shellTrap);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isMeFirstFailMove() {
        return true;
    }

    @Override
    public Boolean isUncallableMove() {
        return true;
    }
}
