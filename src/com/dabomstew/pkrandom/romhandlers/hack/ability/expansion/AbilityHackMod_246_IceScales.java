package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_246_IceScales extends AbilityHackMod {
    public AbilityHackMod_246_IceScales() {
        super(Abilities.iceScales);
    }

    @Override
    public String getName(Context context) {
        return "Ice Scales";
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
