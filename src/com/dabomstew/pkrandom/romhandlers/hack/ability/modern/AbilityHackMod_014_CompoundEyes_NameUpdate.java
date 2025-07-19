package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_014_CompoundEyes_NameUpdate extends AbilityHackMod {
    public AbilityHackMod_014_CompoundEyes_NameUpdate() {
        super(Abilities.compoundEyes);
    }

    @Override
    public String getName(Context context) {
        return "Compound Eyes";
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Compound Eyes, huh...",
                Dialogue.clearLine,
                "This Ability raises a Pokémon's accuracy.",
                Dialogue.clearLine,
                "What's more...",
                Dialogue.clearLine,
                "It raises the chance to encounter",
                "wild Pokémon with held items when",
                "the leading party member has it."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
    }
}
