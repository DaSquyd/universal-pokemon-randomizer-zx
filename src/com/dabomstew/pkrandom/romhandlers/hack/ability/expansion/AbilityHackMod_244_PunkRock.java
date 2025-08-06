package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_244_PunkRock extends AbilityHackMod {
    public AbilityHackMod_244_PunkRock() {
        super(Abilities.punkRock);
    }

    @Override
    public String getName(Context context) {
        return "Punk Rock";
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
