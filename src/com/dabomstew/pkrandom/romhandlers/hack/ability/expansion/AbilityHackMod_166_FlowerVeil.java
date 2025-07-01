package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_166_FlowerVeil extends AbilityHackMod {
    public AbilityHackMod_166_FlowerVeil() {
        super(Abilities.flowerVeil);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Flower Veil";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
