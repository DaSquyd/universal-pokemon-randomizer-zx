package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_542_Hurricane extends MoveHackMod {
    public MoveHackMod_542_Hurricane() {
        super(Moves.hurricane);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckSemiInvulnerable));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onBypassAccuracyCheck, "thunder_bypass_accuracy_check.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracy, "thunder_accuracy.s"));
        
        return true;
    }
}
