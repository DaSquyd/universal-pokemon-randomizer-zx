package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_168_Protean extends AbilityHackMod {
    public final boolean firstOnly;

    public AbilityHackMod_168_Protean(boolean firstOnly) {
        super(Abilities.protean);

        this.firstOnly = firstOnly;
    }

    @Override
    public String getDescription(Context context, List<String> abilityDescriptions) {
        return "Changes type to the match" +
                "the current move.";
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_PROTEAN_FIRST_ONLY", firstOnly);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDecideTarget, "protean.s"));
    }
}
