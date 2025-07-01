package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_182_Pixilate extends AbilityHackMod {
    public AbilityHackMod_182_Pixilate() {
        super(Abilities.pixilate);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Pixilate";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Normal-type moves become\\xFFFEFairy-type moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "pixilate_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "pixilate_power.s"));
    }
}
