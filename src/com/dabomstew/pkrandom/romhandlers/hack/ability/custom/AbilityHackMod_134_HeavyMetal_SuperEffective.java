package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_134_HeavyMetal_SuperEffective extends AbilityHackMod {
    public final double multiplier;
    
    public AbilityHackMod_134_HeavyMetal_SuperEffective(double multiplier) {
        super(Abilities.heavyMetal);
        
        this.multiplier = multiplier;
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Heavy alloys reduce",
                "super effective damage."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_HEAVY_METAL_MULTIPLIER", multiplier);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetWeight));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessing2, "heavy_metal_redux.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
