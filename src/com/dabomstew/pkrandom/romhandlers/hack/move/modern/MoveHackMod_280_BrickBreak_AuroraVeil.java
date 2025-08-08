package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_280_BrickBreak_AuroraVeil extends MoveHackMod {
    public MoveHackMod_280_BrickBreak_AuroraVeil() {
        super(Moves.brickBreak);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessing1));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessingEnd));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveDamage, "common_screen_break.s"));
        
        return true;
    }
}
