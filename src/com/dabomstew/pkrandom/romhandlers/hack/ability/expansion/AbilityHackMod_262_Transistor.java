package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_262_Transistor extends AbilityHackMod {
    public AbilityHackMod_262_Transistor() {
        super(Abilities.transistor);
    }

    @Override
    public String getName(Context context) {
        return "Transistor";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up Electric-type",
                "attacks."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Transistor, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "Electric-type moves."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "transistor.s"));
    }
}
