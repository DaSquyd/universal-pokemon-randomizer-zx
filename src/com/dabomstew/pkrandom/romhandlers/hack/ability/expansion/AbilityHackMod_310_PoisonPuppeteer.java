package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_310_PoisonPuppeteer extends AbilityHackMod {
    public AbilityHackMod_310_PoisonPuppeteer() {
        super(Abilities.poisonPuppeteer);
    }

    @Override
    public String getName(Context context) {
        return "Poison Puppeteer";
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
