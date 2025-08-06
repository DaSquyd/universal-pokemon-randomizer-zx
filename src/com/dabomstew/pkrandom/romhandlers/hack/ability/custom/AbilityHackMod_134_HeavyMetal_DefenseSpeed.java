package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_134_HeavyMetal_DefenseSpeed extends AbilityHackMod {
    private final double defenseMultiplier;
    private final double speedMultiplier;
    
    public AbilityHackMod_134_HeavyMetal_DefenseSpeed(double defenseMultiplier, double speedMultiplier) {
        super(Abilities.heavyMetal);
        
        this.defenseMultiplier = defenseMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts the Defense stat,",
                "but lowers the Speed stat."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_HEAVY_METAL_DEFENSE_MULTIPLIER", defenseMultiplier,
                "ABILITY_HEAVY_METAL_SPEED_MULTIPLIER", speedMultiplier
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetWeight));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStatValue, "heavy_metal_defense.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCalcSpeed, "heavy_metal_speed.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
