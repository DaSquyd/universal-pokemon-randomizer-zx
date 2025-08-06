package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_284_VesselOfRuin extends AbilityHackMod {
    public AbilityHackMod_284_VesselOfRuin() {
        super(Abilities.vesselOfRuin);
    }

    @Override
    public String getName(Context context) {
        return "Vessel of Ruin";
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
