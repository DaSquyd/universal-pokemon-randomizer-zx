package com.dabomstew.pkrandom.romhandlers.hack.move.custom;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_327_SkyUppercut_Flying extends MoveHackMod {
    public MoveHackMod_327_SkyUppercut_Flying() {
        super(Moves.skyUppercut);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckSemiInvulnerable));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetEffectiveness, "sky_uppercut.s"));
        
        return true;
    }
}
