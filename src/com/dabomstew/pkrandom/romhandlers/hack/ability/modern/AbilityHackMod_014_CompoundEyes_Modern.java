package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_014_CompoundEyes_Modern extends AbilityHackMod {
    public AbilityHackMod_014_CompoundEyes_Modern() {
        super(Abilities.compoundEyes);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Compound Eyes";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(number).replace("Compoundeyes", "Compound Eyes");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
    }
}
