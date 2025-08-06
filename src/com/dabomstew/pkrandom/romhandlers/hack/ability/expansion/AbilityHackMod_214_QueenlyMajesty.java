package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_214_QueenlyMajesty extends AbilityHackMod {
    public AbilityHackMod_214_QueenlyMajesty() {
        super(Abilities.queenlyMajesty);
    }

    @Override
    public String getName(Context context) {
        return "Queenly Majesty";
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
