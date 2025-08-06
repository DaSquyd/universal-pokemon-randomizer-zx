package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_201_Berserk extends AbilityHackMod {
    public AbilityHackMod_201_Berserk() {
        super(Abilities.berserk);
    }

    @Override
    public String getName(Context context) {
        return "Berserk";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Raises Sp. Atk when HP",
                "falls below half."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Berserk, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability falls",
                "below half HP, Pokémon's Sp. Atk raises.",
                Dialogue.clearLine,
                "You should know this effect can occur",
                "multiple times a battle."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_Hit2, "berserk.s"));

        return true;
    }
}
