package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_229_GrassySurge extends AbilityHackMod {
    public AbilityHackMod_229_GrassySurge() {
        super(Abilities.grassySurge);
    }

    @Override
    public String getName(Context context) {
        return "Grassy Surge";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Turns the ground into",
                "Grassy Terrain on entry."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Grassy Surge, huh...",
                Dialogue.clearLine,
                "A Pokémon with this Ability makes",
                "the ground Grassy Terrain",
                "when it enters the battle.",
                Dialogue.clearLine,
                "While Grassy Terrain is active,",
                "Grass-type attacks are powered up",
                "and all Pokémon recover some HP",
                "at the end of every turn."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }
}
