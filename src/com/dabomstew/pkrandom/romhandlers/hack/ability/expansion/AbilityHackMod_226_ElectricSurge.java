package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_226_ElectricSurge extends AbilityHackMod {
    public AbilityHackMod_226_ElectricSurge() {
        super(Abilities.electricSurge);
    }

    @Override
    public String getName(Context context) {
        return "Electric Surge";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Turns the ground into",
                "Electric Terrain on entry."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Electric Surge, huh...",
                Dialogue.clearLine,
                "A Pokémon with this Ability makes",
                "the ground Electric Terrain",
                "when it enters the battle.",
                Dialogue.clearLine,
                "While Electric Terrain is active,",
                "Electric-type attacks are powered up",
                "and Pokémon are unable to fall asleep."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }
}
