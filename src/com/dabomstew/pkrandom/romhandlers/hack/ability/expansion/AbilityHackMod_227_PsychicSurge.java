package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_227_PsychicSurge extends AbilityHackMod {
    public AbilityHackMod_227_PsychicSurge() {
        super(Abilities.psychicSurge);
    }

    @Override
    public String getName(Context context) {
        return "Psychic Surge";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Turns the ground into",
                "Psychic Terrain on entry."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Psychic Surge, huh...",
                Dialogue.clearLine,
                "A Pokémon with this Ability makes",
                "the ground Psychic Terrain",
                "when it enters the battle.",
                Dialogue.clearLine,
                "While Psychic Terrain is active,",
                "Psychic-type attacks are powered up",
                "and moves with increased priority",
                "will fail."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }
}
