package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_058_Minus_SpDefBoost extends AbilityHackMod {
    private final boolean keepOldEffect;
    private final double multiplier;
    private final String message;

    public AbilityHackMod_058_Minus_SpDefBoost(boolean keepOldEffect, double multiplier, String message) {
        super(Abilities.minus);

        this.keepOldEffect = keepOldEffect;
        this.multiplier = multiplier;
        this.message = message;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_MINUS_KEEP_OLD_EFFECT", keepOldEffect,
                "ABILITY_MINUS_MULTIPLIER", multiplier
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "minus_spdef.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "minus_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "minus_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "minus_message.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
