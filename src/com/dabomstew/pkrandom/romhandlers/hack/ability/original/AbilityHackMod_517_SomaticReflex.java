package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_517_SomaticReflex extends AbilityHackMod {
    public AbilityHackMod_517_SomaticReflex() {
        super(ParagonLiteAbilities.somaticReflex);
    }

    @Override
    public String getName(Context context) {
        return "Somatic Reflex";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Reacts immediately to",
                "incoming attacks."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Somatic Reflex, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "is hit by any attack,",
                "it will act right away if it hasn't already."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
        
        return true;
    }
}
