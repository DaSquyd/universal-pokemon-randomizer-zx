package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_031_LightningRod_NameUpdate extends AbilityHackMod {
    public AbilityHackMod_031_LightningRod_NameUpdate() {
        super(Abilities.lightningRod);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Lightning Rod";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(number).replace("Lightningrod", "Lightning Rod");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
    }
}
