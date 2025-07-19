package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_077_TangledFeet_OnMissOrFail extends AbilityHackMod {
    public AbilityHackMod_077_TangledFeet_OnMissOrFail() {
        super(Abilities.tangledFeet);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Speed is raised if a move",
                "misses or if confused."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteFail, "tangled_feet_flinch.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteNoEffect, "tangled_feet_miss.s"));
    }
}
