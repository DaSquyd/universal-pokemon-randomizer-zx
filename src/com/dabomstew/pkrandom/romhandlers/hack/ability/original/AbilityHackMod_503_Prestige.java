package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_503_Prestige extends AbilityHackMod {
    public AbilityHackMod_503_Prestige() {
        super(ParagonLiteAbilities.prestige);
    }

    @Override
    public String getName(Context context) {
        return "Prestige";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts Sp. Atk after",
                "knocking out any Pokémon."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Prestige, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "knocks out an opponent,",
                "its Sp. Atk goes up."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "lucky_foot.s"));

        return true;
    }
}
