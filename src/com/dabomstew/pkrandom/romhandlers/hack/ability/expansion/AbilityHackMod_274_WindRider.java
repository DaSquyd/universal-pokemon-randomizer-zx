package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_274_WindRider extends AbilityHackMod {
    public AbilityHackMod_274_WindRider() {
        super(Abilities.windRider);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Wind Rider";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "wind_rider_immunity.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "wind_rider_on_enter.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "wind_rider_on_enter.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.OnMoveExecuteEffective, "wind_rider_after_tailwind.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
