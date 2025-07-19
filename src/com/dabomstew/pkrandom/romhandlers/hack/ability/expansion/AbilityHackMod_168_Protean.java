package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_168_Protean extends AbilityHackMod {
    public final boolean firstOnly;

    public AbilityHackMod_168_Protean() {
        super(Abilities.protean);

        this.firstOnly = true;
    }

    public AbilityHackMod_168_Protean(boolean firstOnly) {
        super(Abilities.protean);

        this.firstOnly = firstOnly;
    }

    @Override
    public String getName(Context context) {
        return "Protean";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "Changes type to the match",
                "the current move."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
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
