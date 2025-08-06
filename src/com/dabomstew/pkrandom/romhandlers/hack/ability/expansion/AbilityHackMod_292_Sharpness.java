package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_292_Sharpness extends AbilityHackMod {
    private final double multiplier;
    
    public AbilityHackMod_292_Sharpness() {
        super(Abilities.sharpness);
        
        this.multiplier = 1.5;
    }
    
    public AbilityHackMod_292_Sharpness(double multiplier) {
        super(Abilities.sharpness);
        
        this.multiplier = multiplier;
    }

    @Override
    public String getName(Context context) {
        return "Sharpness";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up slicing moves.",
                ""
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Sharpness, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "moves that slice the target."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_SHARPNESS_MULTIPLIER", multiplier);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "sharpness.s"));

        return true;
    }
}
