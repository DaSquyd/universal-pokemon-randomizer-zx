package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_300_MindsEye extends AbilityHackMod {
    public AbilityHackMod_300_MindsEye() {
        super(Abilities.mindsEye);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Mind's Eye";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Ignores changes to" +
                "opponents' evasiveness";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // ignore changes to evasion
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracyStage, Abilities.unaware));

        // prevent accuracy drop
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, Abilities.keenEye));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeFail, Abilities.keenEye));
        
        // hit Ghost-type Pokémon with Normal- and Fighting-type moves
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetEffectiveness, Abilities.scrappy));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
