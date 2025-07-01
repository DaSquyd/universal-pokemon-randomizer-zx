package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_507_Cacophony extends AbilityHackMod {
    public AbilityHackMod_507_Cacophony() {
        super(ParagonLiteAbilities.cacophony);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Cacophony";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Harsh noise amplifies the\\xFFFEpower of sound moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "cacophony_boost.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "cacophony_immunity.s"));
    }
}
