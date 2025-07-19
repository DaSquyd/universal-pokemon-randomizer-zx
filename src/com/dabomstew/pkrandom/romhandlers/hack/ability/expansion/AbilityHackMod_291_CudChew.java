package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_291_CudChew extends AbilityHackMod {
    public AbilityHackMod_291_CudChew() {
        super(Abilities.cudChew);
    }

    @Override
    public String getName(Context context) {
        return "Cud Chew";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Eaten berries are spat up",
                "and can be eaten again."
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
