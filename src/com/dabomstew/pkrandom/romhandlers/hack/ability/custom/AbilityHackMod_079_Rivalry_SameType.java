package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_079_Rivalry_SameType extends AbilityHackMod {
    private final double multiplier;

    public AbilityHackMod_079_Rivalry_SameType(double multiplier) {
        super(Abilities.rivalry);

        this.multiplier = multiplier;
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Deals more damage to a",
                "Pokémon of same type."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Rivalry, huh...",
                Dialogue.clearLine,
                "This Ability raises the power of",
                "the Pokémon's move when the target",
                "shares a type."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_RIVALRY_MULTIPLIER", multiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "rivalry_same_type.s"));
    }
}
