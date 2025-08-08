package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_562_Belch extends MoveHackMod {
    public MoveHackMod_562_Belch() {
        super(Moves.belch);
    }

    @Override
    public boolean addAnimation() {
        return true;
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteCheck2, "belch.s"));

        return true;
    }

    @Override
    public Boolean isMeFirstFailMove() {
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
