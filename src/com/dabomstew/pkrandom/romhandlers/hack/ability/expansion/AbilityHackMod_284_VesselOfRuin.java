package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_284_VesselOfRuin extends AbilityHackMod {
    public AbilityHackMod_284_VesselOfRuin() {
        super(Abilities.vesselOfRuin);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Vessel of Ruin";
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
