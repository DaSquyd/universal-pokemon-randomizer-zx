package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_155_Rattled_IntimidateActivation extends AbilityHackMod {
    public AbilityHackMod_155_Rattled_IntimidateActivation() {
        super(Abilities.rattled);
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeSuccess, "rattled_on_intimidate.s"));

        return true;
    }
}
