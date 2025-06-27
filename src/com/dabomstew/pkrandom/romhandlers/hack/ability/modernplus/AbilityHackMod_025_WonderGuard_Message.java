package com.dabomstew.pkrandom.romhandlers.hack.ability.modernplus;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_025_WonderGuard_Message extends AbilityHackMod {
    String message;

    public AbilityHackMod_025_WonderGuard_Message(String message) {
        super(Abilities.wonderGuard);

        this.message = message;
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPreventRun));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "wonder_guard_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "wonder_guard_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "wonder_guard_message.s"));
    }
}
