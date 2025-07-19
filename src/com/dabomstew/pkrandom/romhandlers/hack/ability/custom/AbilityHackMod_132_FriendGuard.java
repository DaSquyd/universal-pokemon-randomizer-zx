package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_132_FriendGuard extends AbilityHackMod {
    private final double multiplier;

    public AbilityHackMod_132_FriendGuard(double multiplier) {
        super(Abilities.friendGuard);

        this.multiplier = multiplier;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_FRIEND_GUARD_MULTIPLIER", multiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessing2, "friend_guard.s"));
    }
}
