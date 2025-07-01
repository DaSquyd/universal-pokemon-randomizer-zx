package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_267_AsOne_GrimNeigh extends AbilityHackMod {
    public AbilityHackMod_267_AsOne_GrimNeigh() {
        super(Abilities.asOneGrimNeigh);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
         return "As One";
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
