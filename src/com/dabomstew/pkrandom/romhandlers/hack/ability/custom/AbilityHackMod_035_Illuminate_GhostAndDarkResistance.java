package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_035_Illuminate_GhostAndDarkResistance extends AbilityHackMod {
    public AbilityHackMod_035_Illuminate_GhostAndDarkResistance() {
        super(Abilities.illuminate);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Ups resistance to Dark-", "and Ghost-type moves.");
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "illuminate_redux.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
