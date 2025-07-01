package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_510_Glazeware extends AbilityHackMod {
    public AbilityHackMod_510_Glazeware() {
        super(ParagonLiteAbilities.glazeware);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Glazeware";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Resistant to Water- and\\xFFFEPoison-type moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.thickFat)
                .replace("Thick Fat", "Glazeware")
                .replace("Fire", "Water")
                .replace("Ice", "Poison");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "glazeware.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
