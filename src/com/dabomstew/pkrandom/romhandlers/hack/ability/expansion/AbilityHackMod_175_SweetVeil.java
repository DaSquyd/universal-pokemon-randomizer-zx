package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_175_SweetVeil extends AbilityHackMod {
    public AbilityHackMod_175_SweetVeil() {
        super(Abilities.sweetVeil);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Sweet Veil";
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
