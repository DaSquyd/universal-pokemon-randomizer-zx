package com.dabomstew.pkrandom.romhandlers.hack.ability.modernplus;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_105_SuperLuck_Message extends AbilityHackMod {
    String message;

    public AbilityHackMod_105_SuperLuck_Message(String message) {
        super(Abilities.superLuck);

        this.message = message;
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetIsCriticalHit));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "super_luck_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "super_luck_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "super_luck_message.s"));
    }
}
