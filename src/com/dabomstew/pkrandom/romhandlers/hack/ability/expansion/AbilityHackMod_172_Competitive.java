package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_172_Competitive extends AbilityHackMod {
    public AbilityHackMod_172_Competitive() {
        super(Abilities.competitive);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Competitive";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "If a stat is lowered,\\xFFFESp. Atk sharply increases.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.defiant)
                .replace("Defiant", "Competitive")
                .replace("Attack", "Sp. Attack");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeSuccess, "competitive.s"));
    }
}
