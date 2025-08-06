package com.dabomstew.pkrandom.romhandlers.hack.ability.modernplus;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_057_Plus_Message extends AbilityHackMod {
    String message;

    public AbilityHackMod_057_Plus_Message(String message) {
        super(Abilities.plus);

        this.message = message;
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "plus_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "plus_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "plus_message.s"));

        return true;
    }
}
