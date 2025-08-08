package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_566_PhantomForce extends MoveHackMod {
    public MoveHackMod_566_PhantomForce() {
        super(Moves.phantomForce);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onChargeUpStart, Moves.shadowForce));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onChargeUpEnd, Moves.shadowForce));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckProtectBreak, Moves.shadowForce));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingStart, Moves.shadowForce));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_Hit1, Moves.shadowForce));

        return true;
    }

    @Override
    public Boolean isSleepTalkUncallableMove() {
        return true;
    }

    @Override
    public Boolean isAssistUncallableMove() {
        return true;
    }
}
