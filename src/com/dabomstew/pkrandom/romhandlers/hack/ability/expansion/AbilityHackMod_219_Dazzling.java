package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_219_Dazzling extends AbilityHackMod {
    public AbilityHackMod_219_Dazzling() {
        super(Abilities.dazzling);
    }

    @Override
    public String getName(Context context) {
        return "Dazzling";
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
