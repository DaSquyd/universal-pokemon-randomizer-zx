package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_267_AsOne_GrimNeigh extends AbilityHackMod {
    public AbilityHackMod_267_AsOne_GrimNeigh() {
        super(Abilities.asOneGrimNeigh);
    }

    @Override
    public String getName(Context context) {
         return "As One";
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }

    @Override
    public Boolean isSkillSwapFail() {
        return true;
    }
}
