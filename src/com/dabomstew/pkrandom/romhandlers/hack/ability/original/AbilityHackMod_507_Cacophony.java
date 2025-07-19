package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_507_Cacophony extends AbilityHackMod {
    public AbilityHackMod_507_Cacophony() {
        super(ParagonLiteAbilities.cacophony);
    }

    @Override
    public String getName(Context context) {
        return "Cacophony";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Harsh noise amplifies the",
                "power of sound moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "cacophony_boost.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "cacophony_immunity.s"));
    }
}
