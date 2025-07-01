package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_206_Galvanize extends AbilityHackMod {
    public AbilityHackMod_206_Galvanize() {
        super(Abilities.galvanize);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Galvanize";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Normal-type moves become\\xFFFEElectric-type moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "galvanize_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "galvanize_power.s"));
    }
}
