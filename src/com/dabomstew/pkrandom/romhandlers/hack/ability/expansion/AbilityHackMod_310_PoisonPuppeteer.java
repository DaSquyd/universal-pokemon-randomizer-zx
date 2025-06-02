package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_310_PoisonPuppeteer extends AbilityHackMod {
    public AbilityHackMod_310_PoisonPuppeteer() {
        super(Abilities.poisonPuppeteer);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isSkillSwapFail() {
        return true;
    }
}
