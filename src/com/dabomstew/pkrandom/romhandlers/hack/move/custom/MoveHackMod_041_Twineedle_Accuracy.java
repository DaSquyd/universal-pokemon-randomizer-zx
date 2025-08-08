package com.dabomstew.pkrandom.romhandlers.hack.move.custom;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_041_Twineedle_Accuracy extends MoveHackMod {
    public MoveHackMod_041_Twineedle_Accuracy() {
        super(Moves.twineedle);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
        
        return true;
    }
}
