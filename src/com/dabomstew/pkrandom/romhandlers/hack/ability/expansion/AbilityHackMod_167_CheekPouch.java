package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_167_CheekPouch extends AbilityHackMod {
    public AbilityHackMod_167_CheekPouch() {
        super(Abilities.cheekPouch);
    }

    @Override
    public String getName(Context context) {
        return "Cheek Pouch";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "Restores HP as well when",
                "the Pokémon eats a Berry."
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
}
