package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_094_SolarPower extends AbilityHackMod {
    private final double multiplier;
    private final int hpFraction;

    public AbilityHackMod_094_SolarPower(double multiplier, int hpFraction) {
        super(Abilities.solarPower);

        this.multiplier = multiplier;
        this.hpFraction = hpFraction;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_SOLAR_POWER_MULTIPLIER", multiplier,
                "ABILITY_SOLAR_POWER_HP_FRACTION", hpFraction
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onWeatherReaction, "solar_power_weather.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "solar_power_spatk_boost.s"));
    }
}
