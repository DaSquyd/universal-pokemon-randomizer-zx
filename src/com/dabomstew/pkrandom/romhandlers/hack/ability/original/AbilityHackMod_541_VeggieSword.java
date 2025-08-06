package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_541_VeggieSword extends AbilityHackMod {
    public AbilityHackMod_541_VeggieSword() {
        super(ParagonLiteAbilities.veggieSword);
    }

    @Override
    public String getName(Context context) {
        return "Veggie Sword";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up Grass-type",
                "attacks."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Veggie Sword, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "Grass-type moves."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "veggie_sword.s"));

        return true;
    }
}
