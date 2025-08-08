package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_282_KnockOff extends MoveHackMod {
    public MoveHackMod_282_KnockOff() {
        super(Moves.knockOff);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_HitReal));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "knock_off.s"));
        
        return true;
    }
}
