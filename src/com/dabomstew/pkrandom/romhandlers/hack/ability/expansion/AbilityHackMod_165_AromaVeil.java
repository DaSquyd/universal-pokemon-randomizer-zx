package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_165_AromaVeil extends AbilityHackMod {
    public AbilityHackMod_165_AromaVeil() {
        super(Abilities.aromaVeil);
    }

    @Override
    public String getName(Context context) {
        return "Aroma Veil";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "Protects allies from moves",
                "that limit move choices."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
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
