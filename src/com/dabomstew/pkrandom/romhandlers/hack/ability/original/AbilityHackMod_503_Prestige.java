package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_503_Prestige extends AbilityHackMod {
    public AbilityHackMod_503_Prestige() {
        super(ParagonLiteAbilities.prestige);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Prestige";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return allDescriptions.get(Abilities.moxie).replace("Attack", "Sp. Atk");
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.moxie)
                .replace("Moxie", "Prestige")
                .replace("Attack", "Sp. Attack");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "lucky_foot.s"));
    }
}
