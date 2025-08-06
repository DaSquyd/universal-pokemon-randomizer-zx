package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_223_PowerOfAlchemy extends AbilityHackMod {
    public AbilityHackMod_223_PowerOfAlchemy() {
        super(Abilities.powerOfAlchemy);
    }

    @Override
    public String getName(Context context) {
        return "Power of Alchemy";
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
}
