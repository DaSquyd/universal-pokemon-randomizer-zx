package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_530_Psionize extends AbilityHackMod {
    public AbilityHackMod_530_Psionize() {
        super(ParagonLiteAbilities.psionize);
    }

    @Override
    public String getName(Context context) {
        return "Psionize";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Normal-type moves become",
                "Psychic-type moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Psionize, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "uses a Normal-type attack,",
                "it becomes Psychic-type instead.",
                Dialogue.clearLine,
                "There's more!",
                Dialogue.clearLine,
                "Attacks that become Psychic-type",
                "also become powered up!",
                Dialogue.clearLine,
                "You should remember that it has",
                "no effect on moves that can",
                "change type on their own."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "psionize_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "psionize_power.s"));
    }
}
