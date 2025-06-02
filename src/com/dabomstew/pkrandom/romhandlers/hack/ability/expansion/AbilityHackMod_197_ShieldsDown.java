package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_197_ShieldsDown extends AbilityHackMod {
    public AbilityHackMod_197_ShieldsDown() {
        super(Abilities.shieldsDown);
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
