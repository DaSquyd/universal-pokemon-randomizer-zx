package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_340_Bounce extends MoveHackMod {
    public MoveHackMod_340_Bounce() {
        super(Moves.bounce);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return true;
    }

    @Override
    public Boolean isAssistUncallableMove() {
        return true;
    }
}
