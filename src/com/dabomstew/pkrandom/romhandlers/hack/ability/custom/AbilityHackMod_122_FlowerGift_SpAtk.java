package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_122_FlowerGift_SpAtk extends AbilityHackMod {
    public AbilityHackMod_122_FlowerGift_SpAtk() {
        super(Abilities.flowerGift);
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Flower Gift, huh...",
                Dialogue.clearLine,
                "When a Cherrim with this Ability",
                "is in sunny weather, its form changes,",
                "and its Sp. Atk and Sp. Def stats go up!",
                Dialogue.clearLine,
                "In Double and Triple Battles, it also",
                "makes its allies' stats increase!"
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostLastSwitchIn));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostWeatherChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityNullified));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onNotifyAirLock));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onActionProcessingEnd));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onTurnCheckDone));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPreAbilityChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "flower_gift_spatk.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStatValue));

        return true;
    }
}
