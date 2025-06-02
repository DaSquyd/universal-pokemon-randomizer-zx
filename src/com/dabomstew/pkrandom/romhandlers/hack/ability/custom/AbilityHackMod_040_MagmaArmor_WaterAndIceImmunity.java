package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_040_MagmaArmor_WaterAndIceImmunity extends AbilityHackMod {
    public AbilityHackMod_040_MagmaArmor_WaterAndIceImmunity() {
        super(Abilities.magmaArmor);
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Immune to Ice- and\n" +
                "Water-type moves.";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAddConditionCheckFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAddConditionFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onActionProcessingEnd));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "magma_armor_redux.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
