package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_040_MagmaArmor_GroundAndWaterResistance extends AbilityHackMod {
    public AbilityHackMod_040_MagmaArmor_GroundAndWaterResistance() {
        super(Abilities.magmaArmor);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Resists Water- and", "Ground-type moves.");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Magma Armor, huh...",
                Dialogue.clearLine,
                "This Ability halves damage from",
                "Water- and Ground-type moves.",
                Dialogue.clearLine,
                "It also has a chance to inflict",
                "the burned status condition",
                "when hit with a direct attack.",
                Dialogue.clearLine,
                "What's more...",
                Dialogue.clearLine,
                "It makes Eggs in your party hatch faster."
        );
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
