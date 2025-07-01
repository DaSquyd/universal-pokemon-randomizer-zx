package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_509_WindWhipper extends AbilityHackMod {
    public AbilityHackMod_509_WindWhipper() {
        super(ParagonLiteAbilities.windWhipper);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Wind Whipper";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Large fans help boost the\\xFFFEpower of wind moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.ironFist)
                .replace("Iron Fist", "Wind Whipper")
                .replace("moves that punch", "wind-based moves");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "wind_whipper.s"));
    }
}
