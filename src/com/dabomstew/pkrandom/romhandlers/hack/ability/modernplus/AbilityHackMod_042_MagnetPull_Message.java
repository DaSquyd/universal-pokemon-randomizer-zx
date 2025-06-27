package com.dabomstew.pkrandom.romhandlers.hack.ability.modernplus;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_042_MagnetPull_Message extends AbilityHackMod {
    String message;

    public AbilityHackMod_042_MagnetPull_Message(String message) {
        super(Abilities.magnetPull);

        this.message = message;
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPreventRun));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "magnet_pull_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "magnet_pull_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "magnet_pull_message.s"));
    }
}
