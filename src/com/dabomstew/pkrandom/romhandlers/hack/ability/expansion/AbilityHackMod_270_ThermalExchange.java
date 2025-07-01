package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_270_ThermalExchange extends AbilityHackMod {
    private final boolean resistFire;

    public AbilityHackMod_270_ThermalExchange() {
        super(Abilities.thermalExchange);
        
        this.resistFire = false;
    }

    public AbilityHackMod_270_ThermalExchange(boolean resistFire) {
        super(Abilities.thermalExchange);
        
        this.resistFire = resistFire;
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Thermal Exchange";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Turns heat into energy,\\xFFFEraising the user's Attack.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // Raise Attack
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "thermal_exchange_boost.s"));
        
        // Immune to burn
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAddConditionCheckFail, Abilities.waterVeil));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAddConditionFail, Abilities.waterVeil));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, Abilities.waterVeil));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, Abilities.waterVeil));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onActionProcessingEnd, Abilities.waterVeil));
        
        if (resistFire)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "thermal_exchange_resist.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
