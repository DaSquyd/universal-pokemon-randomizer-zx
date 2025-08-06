package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_052_HyperCutter_IgnoreDefense extends AbilityHackMod {
    public AbilityHackMod_052_HyperCutter_IgnoreDefense() {
        super(Abilities.hyperCutter);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Slices through Attack and",
                "Defense changes."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, "hyper_cutter_stat_drop.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStat, "hyper_cutter_attacking_stat.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStat, "hyper_cutter_defending_stat.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
