package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_259_QuickDraw extends AbilityHackMod {
    public AbilityHackMod_259_QuickDraw() {
        super(Abilities.quickDraw);
    }

    @Override
    public String getName(Context context) {
        return "Quick Draw";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Enables the Pokémon to",
                "move first occasionally."
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
}
