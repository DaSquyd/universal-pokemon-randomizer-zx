package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_200_Steelworker extends AbilityHackMod {
    public AbilityHackMod_200_Steelworker() {
        super(Abilities.steelworker);
    }

    @Override
    public String getName(Context context) {
        return "Steelworker";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up Steel-type",
                "moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Steelworker, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "Steel-type moves."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "steelworker.s"));

        return true;
    }
}
