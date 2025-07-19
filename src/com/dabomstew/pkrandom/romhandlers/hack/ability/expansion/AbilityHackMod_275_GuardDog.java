package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_275_GuardDog extends AbilityHackMod {
    public AbilityHackMod_275_GuardDog() {
        super(Abilities.guardDog);
    }

    @Override
    public String getName(Context context) {
        return "Guard Dog";
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
