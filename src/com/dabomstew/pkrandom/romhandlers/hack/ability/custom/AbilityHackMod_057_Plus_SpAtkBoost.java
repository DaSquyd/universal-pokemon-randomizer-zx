package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_057_Plus_SpAtkBoost extends AbilityHackMod {
    private final boolean keepOldEffect;
    private final double multiplier;
    private final String message;

    public AbilityHackMod_057_Plus_SpAtkBoost(boolean keepOldEffect, double multiplier, String message) {
        super(Abilities.plus);

        this.keepOldEffect = keepOldEffect;
        this.multiplier = multiplier;
        this.message = message;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_PLUS_MULTIPLIER", multiplier);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "plus_spatk.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "plus_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "plus_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "plus_message.s"));

        return true;
    }
}
