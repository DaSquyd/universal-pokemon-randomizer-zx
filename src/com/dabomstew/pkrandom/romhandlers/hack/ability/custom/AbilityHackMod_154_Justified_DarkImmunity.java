package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_154_Justified_DarkImmunity extends AbilityHackMod {
    public AbilityHackMod_154_Justified_DarkImmunity() {
        super(Abilities.justified);
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Justified, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "is hit by a Dark-type move,",
                "its Attack goes up.",
                Dialogue.clearLine,
                "It also takes no damage or",
                "added effects from those moves."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "justified.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
