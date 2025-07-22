package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_543_Focusing_Lens extends AbilityHackMod {
    public AbilityHackMod_543_Focusing_Lens() {
        super(ParagonLiteAbilities.focusingLens);
    }

    @Override
    public String getName(Context context) {
        return "Focusing Lens";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts the power of light",
                "and beam moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Focusing Lens, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "uses a light or beam move,",
                "the move deals more damage."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "focusing_lens.s"));
    }
}
