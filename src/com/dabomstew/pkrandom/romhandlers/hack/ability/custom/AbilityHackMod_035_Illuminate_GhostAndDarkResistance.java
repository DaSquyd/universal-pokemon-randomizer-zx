package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_035_Illuminate_GhostAndDarkResistance extends AbilityHackMod {
    public AbilityHackMod_035_Illuminate_GhostAndDarkResistance() {
        super(Abilities.illuminate);
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Ups resistance to Dark-\n" +
                "and Ghost-type moves.";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "illuminate_redux.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
