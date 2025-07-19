package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_035_Illuminate_Accuracy extends AbilityHackMod {
    private final double multiplier;
    private final String message;

    public AbilityHackMod_035_Illuminate_Accuracy(double multiplier, String message) {
        super(Abilities.illuminate);

        this.multiplier = multiplier;
        this.message = message;
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Boosts the accuracy of", "all Pokémon on the field.");
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_ILLUMINATE_ACCURACY_MULTIPLIER", multiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "illuminate_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "illuminate_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracy, "illuminate_accuracy.s"));
    }
}
