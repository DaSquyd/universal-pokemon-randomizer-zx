package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_212_Corrosion extends AbilityHackMod {
    public AbilityHackMod_212_Corrosion() {
        super(Abilities.corrosion);
    }

    @Override
    public String getName(Context context) {
        return "Corrosion";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "It can poison even Steel-",
                "or Poison-type targets."
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
