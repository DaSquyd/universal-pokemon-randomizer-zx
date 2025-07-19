package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_526_CoolantBoost extends AbilityHackMod {
    public AbilityHackMod_526_CoolantBoost() {
        super(ParagonLiteAbilities.coolantBoost);
    }

    @Override
    public String getName(Context context) {
        return "Coolant Boost";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up special attacks",
                "when frostbitten."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Coolant Boost, huh...",
                Dialogue.clearLine,
                "When a Pokémon with",
                "this Ability is frostbitten,",
                "its Sp. Atk goes up."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "coolant_boost.s"));
    }
}
