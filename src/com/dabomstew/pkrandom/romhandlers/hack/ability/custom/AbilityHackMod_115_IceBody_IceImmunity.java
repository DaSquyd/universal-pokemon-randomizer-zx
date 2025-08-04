package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_115_IceBody_IceImmunity extends AbilityHackMod {
    public AbilityHackMod_115_IceBody_IceImmunity() {
        super(Abilities.iceBody);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Ice-type moves and",
                "hail restore HP."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Ice Body, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability do not",
                "take damage from Ice-type moves",
                "Their HP is restored a little instead.",
                Dialogue.clearLine,
                "There's more...",
                Dialogue.clearLine,
                "Pokémon with this Ability",
                "recover a little bit of HP every turn",
                "during a hailstorm."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onWeatherReaction));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckNoEffect3, "ice_body_immunity.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
