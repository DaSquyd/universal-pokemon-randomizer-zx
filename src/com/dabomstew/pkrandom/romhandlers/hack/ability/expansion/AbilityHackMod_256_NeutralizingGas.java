package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_256_NeutralizingGas extends AbilityHackMod {
    public AbilityHackMod_256_NeutralizingGas() {
        super(Abilities.neutralizingGas);
    }

    @Override
    public String getName(Context context) {
        return "Neutralizing Gas";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Strange gas blocks all",
                "other Abilities."
        );
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
