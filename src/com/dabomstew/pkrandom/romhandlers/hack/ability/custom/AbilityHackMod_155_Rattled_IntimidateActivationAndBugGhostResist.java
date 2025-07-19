package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_155_Rattled_IntimidateActivationAndBugGhostResist extends AbilityHackMod {
    public AbilityHackMod_155_Rattled_IntimidateActivationAndBugGhostResist() {
        super(Abilities.rattled);
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "rattled_resist_redux.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeSuccess, "rattled_on_intimidate.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
