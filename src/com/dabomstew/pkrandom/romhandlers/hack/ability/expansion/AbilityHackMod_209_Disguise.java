package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_209_Disguise extends AbilityHackMod {
    public AbilityHackMod_209_Disguise() {
        super(Abilities.disguise);
    }

    @Override
    public String getName(Context context) {
        return "Disguise";
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

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
