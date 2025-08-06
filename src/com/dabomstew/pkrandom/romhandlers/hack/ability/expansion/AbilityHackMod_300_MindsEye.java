package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_300_MindsEye extends AbilityHackMod {
    public AbilityHackMod_300_MindsEye() {
        super(Abilities.mindsEye);
    }

    @Override
    public String getName(Context context) {
        return "Mind's Eye";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Ignores changes to", "foe evasiveness");
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // ignore changes to evasion
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveAccuracyStage, Abilities.unaware));

        // prevent accuracy drop
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, Abilities.keenEye));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeFail, Abilities.keenEye));

        // hit Ghost-type Pokémon with Normal- and Fighting-type moves
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetEffectiveness, Abilities.scrappy));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
