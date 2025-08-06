package com.dabomstew.pkrandom.romhandlers.hack.ability.modernplus;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_071_ArenaTrap_Message extends AbilityHackMod {
    String message;

    public AbilityHackMod_071_ArenaTrap_Message(String message) {
        super(Abilities.arenaTrap);

        this.message = message;
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPreventRun));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "arena_trap_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "arena_trap_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "arena_trap_message.s"));

        return true;
    }
}
