package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_001_Stench extends AbilityHackMod {

    private final int percentChance;

    public AbilityHackMod_001_Stench(int percentChance) {
        super(Abilities.stench);

        if (percentChance < 1 || percentChance > 100)
            throw new RuntimeException("Must be between 1 and 100");

        this.percentChance = percentChance;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_STENCH_FLINCH_PERCENT", percentChance);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        addGlobalValue(context, "ABILITY_STENCH_FLINCH_PERCENT", percentChance);
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveFlinchChance, "stench.s"));
    }
}
