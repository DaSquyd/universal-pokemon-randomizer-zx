package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_225_RksSystem extends AbilityHackMod {
    public AbilityHackMod_225_RksSystem() {
        super(Abilities.rksSystem);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
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
