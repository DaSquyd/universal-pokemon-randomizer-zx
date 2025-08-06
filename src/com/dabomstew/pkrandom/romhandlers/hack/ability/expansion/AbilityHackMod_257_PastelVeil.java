package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_257_PastelVeil extends AbilityHackMod {
    public AbilityHackMod_257_PastelVeil() {
        super(Abilities.pastelVeil);
    }

    @Override
    public String getName(Context context) {
        return "Pastel Veil";
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
