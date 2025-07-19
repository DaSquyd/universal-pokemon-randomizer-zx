package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_173_StrongJaw extends AbilityHackMod {
    private final double multiplier;
    
    public AbilityHackMod_173_StrongJaw() {
        super(Abilities.strongJaw);
        
        this.multiplier = 1.5;
    }
    
    public AbilityHackMod_173_StrongJaw(double multiplier) {
        super(Abilities.strongJaw);
        
        this.multiplier = multiplier;
    }

    @Override
    public String getName(Context context) {
        return "Strong Jaw";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "Its strong jaw boosts the",
                "power of its biting moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Strong Jaw, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "moves that bite."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_STRONG_JAW_MULTIPLIER", multiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "strong_jaw.s"));
    }
}
