package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_180_Symbiosis extends AbilityHackMod {    
    public AbilityHackMod_180_Symbiosis() {
        super(Abilities.symbiosis);
    }

    @Override
    public String getName(Context context) {
        return "Symbiosis";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("The Pokémon can pass", "an item to an ally.");
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
