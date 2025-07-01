package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_505_Assimilate extends AbilityHackMod {
    public AbilityHackMod_505_Assimilate() {
        super(ParagonLiteAbilities.assimilate);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Assimilate";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Boosts Sp. Atk when hit by\\xFFFEa Psychic-type move.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.sapSipper)
                .replace("Sap Sipper", "Assimilate")
                .replace("Grass", "Psychic")
                .replace("Attack", "Sp. Attack");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "assimilate.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
