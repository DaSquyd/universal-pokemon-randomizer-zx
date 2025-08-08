package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_076_SolarBeam extends MoveHackMod {
    public MoveHackMod_076_SolarBeam() {
        super(Moves.solarBeam);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckChargeUpSkip, "solar_beam_charge_up_skip.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onChargeUpStart));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "solar_beam_move_power.s"));
        
        return true;
    }
}
