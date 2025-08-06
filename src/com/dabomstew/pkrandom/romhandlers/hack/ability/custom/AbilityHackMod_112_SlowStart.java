package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_112_SlowStart extends AbilityHackMod {
    private final int turns;

    public AbilityHackMod_112_SlowStart(int turns) {
        super(Abilities.slowStart);

        this.turns = turns;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_SLOW_START_TURNS", turns);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCalcSpeed));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onTurnCheckEnd, "slow_start_end_of_turn.s"));

        return true;
    }
}
