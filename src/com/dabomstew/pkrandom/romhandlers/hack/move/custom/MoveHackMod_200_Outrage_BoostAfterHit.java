package com.dabomstew.pkrandom.romhandlers.hack.move.custom;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_200_Outrage_BoostAfterHit extends MoveHackMod {
    public MoveHackMod_200_Outrage_BoostAfterHit() {
        super(Moves.outrage);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveBasePower, Moves.revenge));

        return true;
    }
}
