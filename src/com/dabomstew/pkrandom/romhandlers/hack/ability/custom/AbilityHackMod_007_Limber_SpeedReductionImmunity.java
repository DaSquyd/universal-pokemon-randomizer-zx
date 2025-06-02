package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AbilityHackMod_007_Limber_SpeedReductionImmunity extends AbilityHackMod {
    public AbilityHackMod_007_Limber_SpeedReductionImmunity() {
        super(Abilities.limber);
    }

    @Override
    public String getDescription(Context context, List<String> abilityDescriptions) {
        return "Guards against paralysis\\xFFFE" +
                "and Speed drops.";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAddConditionCheckFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAddConditionFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onActionProcessingEnd));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, "limber_speed.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeFail, "limber_speed_message.s"));
    }
}
