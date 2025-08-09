package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_528_ScaredyCat extends AbilityHackMod {
    public AbilityHackMod_528_ScaredyCat() {
        super(ParagonLiteAbilities.scaredyCat);
    }

    @Override
    public String getName(Context context) {
        return "Scaredy Cat";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Switches out when its",
                "stats are lowered."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChange, "scaredy_cat.s"));
        return true;
    }
}
