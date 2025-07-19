package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_079_Rivalry extends AbilityHackMod {
    private final double sameGenderMultiplier;
    private final double oppositeGenderMultiplier;

    public AbilityHackMod_079_Rivalry(double sameGenderMultiplier, double oppositeGenderMultiplier) {
        super(Abilities.rivalry);

        this.sameGenderMultiplier = sameGenderMultiplier;
        this.oppositeGenderMultiplier = oppositeGenderMultiplier;
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Rivalry, huh...",
                Dialogue.clearLine,
                "This Ability raises the power of",
                "the Pokémon's move when the target is",
                "of the same gender."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_RIVALRY_SAME_GENDER_MULTIPLIER", sameGenderMultiplier,
                "ABILITY_RIVALRY_OPPOSITE_GENDER_MULTIPLIER", oppositeGenderMultiplier
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "rivalry.s"));
    }
}
