package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_500_HeavyWing extends AbilityHackMod {
    public AbilityHackMod_500_HeavyWing() {
        super(ParagonLiteAbilities.heavyWing);
    }

    @Override
    public String getName(Context context) {
        return "Heavy Wing";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powerful wings boost",
                "Flying-type moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Heavy Wing, huh...",
                Dialogue.clearLine,
                "This Ability increases the power",
                "of Flying-type moves."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "heavy_wing.s"));

        return true;
    }
}
