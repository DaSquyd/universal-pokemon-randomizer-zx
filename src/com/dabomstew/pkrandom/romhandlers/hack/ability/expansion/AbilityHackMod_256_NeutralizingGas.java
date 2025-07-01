package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_256_NeutralizingGas extends AbilityHackMod {
    public AbilityHackMod_256_NeutralizingGas() {
        super(Abilities.neutralizingGas);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Neutralizing Gas";
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
