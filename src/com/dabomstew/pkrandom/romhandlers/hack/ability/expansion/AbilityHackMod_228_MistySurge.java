package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_228_MistySurge extends AbilityHackMod {
    public AbilityHackMod_228_MistySurge() {
        super(Abilities.mistySurge);
    }

    @Override
    public String getName(Context context) {
        return "Misty Surge";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Turns the ground into",
                "Misty Terrain on entry."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Misty Surge, huh...",
                Dialogue.clearLine,
                "A Pokémon with this Ability makes",
                "the ground Misty Terrain",
                "when it enters the battle.",
                Dialogue.clearLine,
                "While Misty Terrain is active,",
                "damage from Dragon-type attacks is halved",
                "and Pokémon cannot be afflicted",
                "with status conditions."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }
}
