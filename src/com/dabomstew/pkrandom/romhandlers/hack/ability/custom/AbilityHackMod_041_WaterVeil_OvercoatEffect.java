package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_041_WaterVeil_OvercoatEffect extends AbilityHackMod {
    public AbilityHackMod_041_WaterVeil_OvercoatEffect() {
        super(Abilities.waterVeil);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Guards the Pokémon from",
                "the weather and burns."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAddConditionCheckFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAddConditionFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onActionProcessingEnd));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onWeatherReaction, Abilities.overcoat));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
