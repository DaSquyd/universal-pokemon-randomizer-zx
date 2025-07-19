package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_522_VolcanicFury extends AbilityHackMod {
    public AbilityHackMod_522_VolcanicFury() {
        super(ParagonLiteAbilities.volcanicFury);
    }

    @Override
    public String getName(Context context) {
        return "Volcanic Fury";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Moves always inflict burns",
                "when HP is half or less."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Volcanic Fury, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "lands a hit while its HP is half or less,",
                "the target is burned."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "volcanic_fury.s"));
    }
}
