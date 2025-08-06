package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_166_FlowerVeil extends AbilityHackMod {
    public AbilityHackMod_166_FlowerVeil() {
        super(Abilities.flowerVeil);
    }

    @Override
    public String getName(Context context) {
        return "Flower Veil";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "Prevents lowering of",
                "Grass-type ally stats."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
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
