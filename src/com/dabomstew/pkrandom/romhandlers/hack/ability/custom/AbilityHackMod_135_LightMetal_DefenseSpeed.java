package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_135_LightMetal_DefenseSpeed extends AbilityHackMod {
    private final double defenseMultiplier;
    private final double speedMultiplier;

    public AbilityHackMod_135_LightMetal_DefenseSpeed(double defenseMultiplier, double speedMultiplier) {
        super(Abilities.lightMetal);

        this.defenseMultiplier = defenseMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts the Speed stat,",
                "but lowers the Defense stat."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_LIGHT_METAL_DEFENSE_MULTIPLIER", defenseMultiplier,
                "ABILITY_LIGHT_METAL_SPEED_MULTIPLIER", speedMultiplier
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetWeight));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStatValue, "light_metal_defense.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCalcSpeed, "light_metal_speed.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
