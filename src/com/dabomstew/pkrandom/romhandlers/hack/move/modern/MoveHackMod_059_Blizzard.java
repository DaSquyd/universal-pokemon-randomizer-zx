package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_059_Blizzard extends MoveHackMod {
    public MoveHackMod_059_Blizzard() {
        super(Moves.blizzard);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onBypassAccuracyCheck, "blizzard.s"));
        
        return true;
    }
}
