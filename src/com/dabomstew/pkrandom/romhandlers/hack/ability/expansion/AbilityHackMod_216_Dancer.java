package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_216_Dancer extends AbilityHackMod {
    public AbilityHackMod_216_Dancer() {
        super(Abilities.dancer);
    }

    @Override
    public String getName(Context context) {
        return "Dancer";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "If a dance is preformed,",
                "the user immediately joins."
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
