package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_286_TabletsOfRuin extends AbilityHackMod {
    public AbilityHackMod_286_TabletsOfRuin() {
        super(Abilities.tabletsOfRuin);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Tablets of Ruin";
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
