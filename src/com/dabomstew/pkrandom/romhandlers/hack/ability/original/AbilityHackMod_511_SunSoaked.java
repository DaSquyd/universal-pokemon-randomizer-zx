package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_511_SunSoaked extends AbilityHackMod {
    public AbilityHackMod_511_SunSoaked() {
        super(ParagonLiteAbilities.sunSoaked);
    }

    @Override
    public String getName(Context context) {
        return "Sun-Soaked";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "The user feels the sun's",
                "energy in any weather."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_SUN_SOAKED_IS_SUN_EFFECT", true);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // full effect in get_effective_weather.s
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "sun-soaked_fire_resistance.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
