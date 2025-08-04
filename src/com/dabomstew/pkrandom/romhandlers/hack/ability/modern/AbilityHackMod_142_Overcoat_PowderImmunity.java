package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_142_Overcoat_PowderImmunity extends AbilityHackMod {
    public AbilityHackMod_142_Overcoat_PowderImmunity() {
        super(Abilities.overcoat);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onWeatherReaction));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckNoEffect3, "overcoat_powder_immunity.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
