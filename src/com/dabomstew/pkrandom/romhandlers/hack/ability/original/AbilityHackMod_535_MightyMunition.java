package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_535_MightyMunition extends AbilityHackMod {
    private final double multiplier;

    public AbilityHackMod_535_MightyMunition() {
        super(ParagonLiteAbilities.mightyMunition);

        this.multiplier = 1.3;
    }

    public AbilityHackMod_535_MightyMunition(double multiplier) {
        super(ParagonLiteAbilities.mightyMunition);
        
        this.multiplier = multiplier;
    }

    @Override
    public String getName(Context context) {
        return "Mighty Munition";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "Powers up ball and",
                "bomb moves."

        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Mighty Munition, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "ball and bomb moves."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_MIGHTY_MUNITION_MULTIPLIER", multiplier);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "mighty_munition.s"));

        return true;
    }
}
