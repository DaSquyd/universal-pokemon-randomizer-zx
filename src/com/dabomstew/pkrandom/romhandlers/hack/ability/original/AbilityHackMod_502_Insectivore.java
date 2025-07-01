package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_502_Insectivore extends AbilityHackMod {
    public AbilityHackMod_502_Insectivore() {
        super(ParagonLiteAbilities.insectivore);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Insectivore";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return allDescriptions.get(Abilities.waterAbsorb).replace("Water", "Bug");
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.waterAbsorb)
                .replace("Water Absorb", "Insectivore")
                .replace("Water", "Bug");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "insectivore.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
