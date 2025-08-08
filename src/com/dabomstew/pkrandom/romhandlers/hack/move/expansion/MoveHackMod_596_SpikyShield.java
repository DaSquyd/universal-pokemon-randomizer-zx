package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_596_SpikyShield extends MoveHackMod {
    public MoveHackMod_596_SpikyShield() {
        super(Moves.spikyShield);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveSequenceStart, Moves.protect));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteCheck2, Moves.protect));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteFail, Moves.protect));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onUncategorizedMove, Moves.protect));

        return true;
    }

    @Override
    public Boolean isProtectionMove() {
        return true;
    }

    @Override
    public Boolean isAssistUncallableMove() {
        return true;
    }

    @Override
    public Boolean isCopycatUncallableMove() {
        return true;
    }
}
