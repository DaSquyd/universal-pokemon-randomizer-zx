package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_225_RksSystem extends AbilityHackMod {
    public AbilityHackMod_225_RksSystem() {
        super(Abilities.rksSystem);
    }

    @Override
    public String getName(Context context) {
        return "RKS System";
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
