package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_091_Adaptability_NonStab extends AbilityHackMod {
    public AbilityHackMod_091_Adaptability_NonStab() {
        super(Abilities.adaptability);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up moves of",
                "types that it isn't."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Adaptability, huh...",
                Dialogue.clearLine,
                "This Ability powers up moves that",
                "don't match the user's type."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessing2, "adaptability.s"));

        return true;
    }
}
