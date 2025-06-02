package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_040_MagmaArmor_GroundAndWaterResistance extends AbilityHackMod {
    public AbilityHackMod_040_MagmaArmor_GroundAndWaterResistance() {
        super(Abilities.magmaArmor);
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Resists Water- and\n" +
                "Ground-type moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return "Magma Armor, huh...\uF000븁\\x0000\\xFFFE"
                + "This Ability halves damage from\\xFFFEWater- and Ground-type moves.\uF000븁\\x0000"
                + "It also has a small chance to inflict\\xFFFEthe burned status condition\uF000븀\\x0000\\xFFFE"
                + "when hit with a direct attack.\uF000븁\\x0000\\xFFFE"
                + "What's more...\uF000븁\\x0000\\xFFFE"
                + "It makes Eggs in your party hatch faster.\uF000븁\\x0000";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "magma_armor_burn.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "magma_armor_resist.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
