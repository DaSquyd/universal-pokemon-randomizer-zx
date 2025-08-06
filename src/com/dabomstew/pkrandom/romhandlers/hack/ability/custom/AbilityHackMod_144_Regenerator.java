package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_144_Regenerator extends AbilityHackMod {
    private final int hpFraction;

    public AbilityHackMod_144_Regenerator(int hpFraction) {
        super(Abilities.regenerator);

        this.hpFraction = hpFraction;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_REGENERATOR_HP_FRACTION", hpFraction);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchOutEnd, "regenerator.s"));

        return true;
    }
}
