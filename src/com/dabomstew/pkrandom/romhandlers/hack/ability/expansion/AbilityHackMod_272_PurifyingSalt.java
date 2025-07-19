package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_272_PurifyingSalt extends AbilityHackMod {
    public AbilityHackMod_272_PurifyingSalt() {
        super(Abilities.purifyingSalt);
    }

    @Override
    public String getName(Context context) {
        return "Purifying Salt";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
