package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_023_ShadowTag_Message extends AbilityHackMod {
    String message;

    public AbilityHackMod_023_ShadowTag_Message(String message) {
        super(Abilities.shadowTag);

        this.message = message;
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPreventRun));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "shadow_tag_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "shadow_tag_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "shadow_tag_message.s"));
    }
}
