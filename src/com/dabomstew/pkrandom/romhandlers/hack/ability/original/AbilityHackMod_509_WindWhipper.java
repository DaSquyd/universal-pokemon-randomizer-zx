package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_509_WindWhipper extends AbilityHackMod {
    public AbilityHackMod_509_WindWhipper() {
        super(ParagonLiteAbilities.windWhipper);
    }

    @Override
    public String getName(Context context) {
        return "Wind Whipper";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Large fans help boost the",
                "power of wind moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Wind Whipper, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "wind moves."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "wind_whipper.s"));

        return true;
    }
}
