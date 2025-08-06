package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_269_SeedSower extends AbilityHackMod {
    public AbilityHackMod_269_SeedSower() {
        super(Abilities.seedSower);
    }

    @Override
    public String getName(Context context) {
         return "Seed Sower";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Turns the ground into",
                "Grassy Terrain when hit."
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
