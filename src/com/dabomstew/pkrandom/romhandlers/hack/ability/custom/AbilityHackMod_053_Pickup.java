package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_053_Pickup extends AbilityHackMod {
    public AbilityHackMod_053_Pickup() {
        super(Abilities.pickup);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onTurnCheckEnd));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "pickup_redux.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "pickup_redux.s"));

        return true;
    }
}
