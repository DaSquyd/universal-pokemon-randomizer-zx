package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_039_InnerFocus_IntimidateImmunity extends AbilityHackMod {
    public AbilityHackMod_039_InnerFocus_IntimidateImmunity() {
        super(Abilities.ownTempo);
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onFlinchCheck));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, "common_intimidate_immunity.s"));
    }
}
