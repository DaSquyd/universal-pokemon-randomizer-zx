package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_289_HadronEngine extends AbilityHackMod {
    public AbilityHackMod_289_HadronEngine() {
        super(Abilities.HadronEngine);
    }

    @Override
    public String getName(Context context) {
        return "Hadron Engine";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isSkillSwapFail() {
        return true;
    }
}
