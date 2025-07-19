package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_113_Scrappy_IntimidateImmunity extends AbilityHackMod {
    public AbilityHackMod_113_Scrappy_IntimidateImmunity() {
        super(Abilities.scrappy);
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetEffectiveness));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, "common_intimidate_immunity.s"));
    }
}
