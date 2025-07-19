package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_501_Specialized extends AbilityHackMod {
    public AbilityHackMod_501_Specialized() {
        super(ParagonLiteAbilities.specialized);
    }

    @Override
    public String getName(Context context) {
        return "Specialized";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up moves of the",
                "same type as the Pokémon."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Specialized, huh...",
                Dialogue.clearLine,
                "When the Pokémon uses a move that",
                "matches its type, this Ability increases",
                "the power boost."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onApplySTAB, "specialized.s"));
    }
}
