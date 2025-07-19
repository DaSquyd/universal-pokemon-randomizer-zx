package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_006_Damp_FireResist extends AbilityHackMod {
    public AbilityHackMod_006_Damp_FireResist() {
        super(Abilities.damp);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Guards against Fire and", "self-destructing moves.");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // Old Damp
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteCheck2));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveSequenceStart));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveSequenceEnd));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityNullified));

        // Old Heatproof
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, Abilities.heatproof));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onConditionDamage, Abilities.heatproof));
    }
}
