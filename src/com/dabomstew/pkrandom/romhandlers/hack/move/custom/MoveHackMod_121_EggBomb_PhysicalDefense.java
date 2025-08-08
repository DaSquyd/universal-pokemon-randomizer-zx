package com.dabomstew.pkrandom.romhandlers.hack.move.custom;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_121_EggBomb_PhysicalDefense extends MoveHackMod {
    public MoveHackMod_121_EggBomb_PhysicalDefense() {
        super(Moves.eggBomb);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStat, Moves.psyshock));
        
        return true;
    }
}
