package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_501_Specialized extends AbilityHackMod {
    public AbilityHackMod_501_Specialized() {
        super(ParagonLiteAbilities.specialized);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Specialized";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return allDescriptions.get(Abilities.adaptability);
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.adaptability).replace("Adaptability", "Specialized");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onApplySTAB, "specialized.s"));
    }
}
