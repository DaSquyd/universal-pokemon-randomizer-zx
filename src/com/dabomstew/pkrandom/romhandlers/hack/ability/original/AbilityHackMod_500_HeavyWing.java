package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_500_HeavyWing extends AbilityHackMod {
    public AbilityHackMod_500_HeavyWing() {
        super(ParagonLiteAbilities.heavyWing);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Heavy Wing";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Powerful wings boost\\xFFFEFlying-type moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.ironFist)
                .replace("Iron Fist", "Heavy Wing")
                .replace("moves that punch", "Flying-type moves");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "heavy_wing.s"));
    }
}
