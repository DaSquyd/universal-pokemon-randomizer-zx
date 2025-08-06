package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_505_Assimilate extends AbilityHackMod {
    public AbilityHackMod_505_Assimilate() {
        super(ParagonLiteAbilities.assimilate);
    }

    @Override
    public String getName(Context context) {
        return "Assimilate";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts Sp. Atk when hit by",
                "a Psychic-type move."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Assimilate, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "is hit by a Psychic-type move,",
                "its Sp. Atk goes up.",
                Dialogue.clearLine,
                "It also takes no damage or",
                "added effects from those moves."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckNoEffect3, "assimilate.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
