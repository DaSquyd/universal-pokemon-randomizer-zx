package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_055_Hustle extends AbilityHackMod {

    private final int accuracyMultiplier;

    public AbilityHackMod_055_Hustle(int accuracyMultiplier) {
        super(Abilities.hustle);

        this.accuracyMultiplier = accuracyMultiplier;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_HUSTLE_ACCURACY_MULTIPLIER", accuracyMultiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracy, "hustle_accuracy.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower));
    }
}
