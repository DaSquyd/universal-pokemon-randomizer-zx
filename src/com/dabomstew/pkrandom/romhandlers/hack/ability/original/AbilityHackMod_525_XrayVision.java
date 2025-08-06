package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_525_XrayVision extends AbilityHackMod {
    public AbilityHackMod_525_XrayVision() {
        super(ParagonLiteAbilities.xrayVision);
    }

    @Override
    public String getName(Context context) {
        return "X-ray Vision";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "The Pokémon can scan",
                "the foe's held item."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "X-ray Vision, huh...",
                Dialogue.clearLine,
                "A Pokémon with this Ability",
                "will tell you what item",
                "an opponent is holding."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "x-ray_vision.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "x-ray_vision.s"));

        return true;
    }
}
