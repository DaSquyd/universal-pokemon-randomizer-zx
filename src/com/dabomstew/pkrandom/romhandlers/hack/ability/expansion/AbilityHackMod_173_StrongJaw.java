package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_173_StrongJaw extends AbilityHackMod {
    public AbilityHackMod_173_StrongJaw() {
        super(Abilities.strongJaw);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Strong Jaw";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Its strong jaw boosts the\\xFFFEpower of its biting moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.ironFist)
                .replace("Iron Fist", "Strong Jaw")
                .replace("moves that punch", "moves that bite");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "strong_jaw.s"));
    }
}
