package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_248_IceFace extends AbilityHackMod {
    public AbilityHackMod_248_IceFace() {
        super(Abilities.iceFace);
    }

    @Override
    public String getName(Context context) {
        return "Ice Face";
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }

    @Override
    public Boolean isRolePlayFail() {
        return true;
    }

    @Override
    public Boolean isSkillSwapFail() {
        return true;
    }
}
