package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_051_KeenEye extends AbilityHackMod {
    private final boolean ignoreEvasion;
    private final double accuracyMultiplier;
    
    public AbilityHackMod_051_KeenEye(boolean ignoreEvasion) {
        super(Abilities.keenEye);
        
        this.ignoreEvasion = ignoreEvasion;
        this.accuracyMultiplier = 1;
    }
    
    public AbilityHackMod_051_KeenEye(boolean ignoreEvasion, double accuracyMultiplier) {
        super(Abilities.keenEye);
        
        this.ignoreEvasion = ignoreEvasion;
        this.accuracyMultiplier = accuracyMultiplier;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_KEEN_EYE_ACCURACY_MULTIPLIER", accuracyMultiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeFail));
        
        if (ignoreEvasion)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracyStage, Abilities.unaware));

        if (accuracyMultiplier != 1)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracy, "keen_eye_move_accuracy.s"));
    }
}
