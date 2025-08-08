package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_011_ViseGrip extends MoveHackMod {
    public MoveHackMod_011_ViseGrip() {
        super(Moves.viseGrip);
    }

    @Override
    public String getName(Context context) {
        return "Vise Grip";
    }

    @Override
    public String getAuxiliaryAnimationLabel() {
        return "Trap";
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return true;
    }
}
