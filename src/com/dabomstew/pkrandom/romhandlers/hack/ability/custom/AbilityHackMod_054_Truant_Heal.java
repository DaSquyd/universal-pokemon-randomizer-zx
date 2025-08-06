package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_054_Truant_Heal extends AbilityHackMod {
    private final boolean healOnMoveTurns;
    private final boolean healOnLoafingTurns;
    private final int healFraction;
    private final boolean ignoreFailedMoves;
    
    public AbilityHackMod_054_Truant_Heal(boolean healOnMoveTurns, boolean healOnLoafingTurns, int healFraction, boolean ignoreFailedMoves) {
        super(Abilities.truant);
        
        this.healOnMoveTurns = healOnMoveTurns;
        this.healOnLoafingTurns = healOnLoafingTurns;
        this.healFraction = healFraction;
        this.ignoreFailedMoves = ignoreFailedMoves;
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_TRUANT_HEAL_ON_MOVE_TURNS", healOnMoveTurns,
                "ABILITY_TRUANT_HEAL_ON_LOAFING_TURNS", healOnLoafingTurns,
                "ABILITY_TRUANT_HEAL_FRACTION", healFraction
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteCheck1));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteFail, "truant_on_fail.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onActionProcessingEnd));
        
        if (ignoreFailedMoves)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteNoEffect, "truant_on_no_effect.s"));
        
        if (healOnMoveTurns && healOnLoafingTurns)
            inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteNoEffect, "truant_every_turn_heal.s"));

        return true;
    }
}
