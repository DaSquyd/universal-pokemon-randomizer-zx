package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_134_HeavyMetal_SuperEffective extends AbilityHackMod {
    public AbilityHackMod_134_HeavyMetal_SuperEffective() {
        super(Abilities.heavyMetal);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Heavy alloys reduce",
                "super effective damage."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetWeight));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessing2, "heavy_metal_redux.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
