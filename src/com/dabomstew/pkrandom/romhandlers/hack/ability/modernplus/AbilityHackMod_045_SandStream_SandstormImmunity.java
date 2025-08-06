package com.dabomstew.pkrandom.romhandlers.hack.ability.modernplus;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_045_SandStream_SandstormImmunity extends AbilityHackMod {
    public AbilityHackMod_045_SandStream_SandstormImmunity() {
        super(Abilities.sandStream);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onWeatherReaction, "sand_stream_no_damage.s"));

        return true;
    }
}
