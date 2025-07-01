package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_308_TeraShell extends AbilityHackMod {
    public AbilityHackMod_308_TeraShell() {
        super(Abilities.teraShell);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Tera Shell";
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
