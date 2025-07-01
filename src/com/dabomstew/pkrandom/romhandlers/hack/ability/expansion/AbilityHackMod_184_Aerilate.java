package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_184_Aerilate extends AbilityHackMod {
    public AbilityHackMod_184_Aerilate() {
        super(Abilities.aerilate);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Aerilate";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Normal-type moves become\\xFFFEFlying-type moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "aerilate_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "aerilate_power.s"));
    }
}
