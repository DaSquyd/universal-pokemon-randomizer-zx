package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_236_Moonlight extends MoveHackMod {
    public MoveHackMod_236_Moonlight() {
        super(Moves.moonlight);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRecoverHealth, "morning_sun.s"));
        
        return true;
    }
}
