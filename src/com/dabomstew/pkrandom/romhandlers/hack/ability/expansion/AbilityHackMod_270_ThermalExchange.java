package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

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
    public String getName(Context context) {
        return "Thermal Exchange";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Turns heat into energy,",
                "raising the user's Attack."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
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
