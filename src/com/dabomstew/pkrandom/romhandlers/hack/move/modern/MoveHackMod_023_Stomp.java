package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_023_Stomp extends MoveHackMod {
    public MoveHackMod_023_Stomp() {
        super(Moves.stomp);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return true;
    }
}
