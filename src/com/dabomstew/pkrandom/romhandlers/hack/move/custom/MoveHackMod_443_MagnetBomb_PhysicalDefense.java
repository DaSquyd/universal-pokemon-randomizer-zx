package com.dabomstew.pkrandom.romhandlers.hack.move.custom;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_443_MagnetBomb_PhysicalDefense extends MoveHackMod {
    public MoveHackMod_443_MagnetBomb_PhysicalDefense() {
        super(Moves.magnetBomb);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStat, Moves.psyshock));
        
        return true;
    }
}
