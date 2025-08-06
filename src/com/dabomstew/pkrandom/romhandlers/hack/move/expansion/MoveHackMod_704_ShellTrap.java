package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_704_ShellTrap extends MoveHackMod {
    public MoveHackMod_704_ShellTrap() {
        super(Moves.shellTrap);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
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
