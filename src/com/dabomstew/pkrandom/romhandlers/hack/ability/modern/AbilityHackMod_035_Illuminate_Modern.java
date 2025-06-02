package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_035_Illuminate_Modern extends AbilityHackMod {
    public AbilityHackMod_035_Illuminate_Modern() {
        super(Abilities.illuminate);
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "It prevents its accuracy\n" +
                "from being lowered.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // ignore changes to evasion
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracyStage, Abilities.unaware));

        // prevent accuracy drop
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, Abilities.keenEye));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeFail, Abilities.keenEye));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
