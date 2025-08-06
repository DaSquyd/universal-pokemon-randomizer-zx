package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_288_OrichalcumPulse extends AbilityHackMod {
    public AbilityHackMod_288_OrichalcumPulse() {
        super(Abilities.OrichalcumPulse);
    }

    @Override
    public String getName(Context context) {
        return "Orichalcum Pulse";
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }

    @Override
    public Boolean isSkillSwapFail() {
        return true;
    }
}
