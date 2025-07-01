package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_266_AsOne_ChillingNeigh extends AbilityHackMod {
    public AbilityHackMod_266_AsOne_ChillingNeigh() {
        super(Abilities.asOneChillingNeigh);
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
