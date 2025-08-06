package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_035_Illuminate_Modern extends AbilityHackMod {
    public AbilityHackMod_035_Illuminate_Modern() {
        super(Abilities.illuminate);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "It prevents its accuracy",
                "from being lowered."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // ignore changes to evasion
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracyStage, Abilities.unaware));

        // prevent accuracy drop
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, Abilities.keenEye));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeFail, Abilities.keenEye));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
