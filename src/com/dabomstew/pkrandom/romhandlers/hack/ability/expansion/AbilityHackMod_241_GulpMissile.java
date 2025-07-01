package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_241_GulpMissile extends AbilityHackMod {
    public AbilityHackMod_241_GulpMissile() {
        super(Abilities.gulpMissile);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Gulp Missile";
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
