package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_165_AromaVeil extends AbilityHackMod {
    public AbilityHackMod_165_AromaVeil() {
        super(Abilities.auraBreak);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Aroma Veil";
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
