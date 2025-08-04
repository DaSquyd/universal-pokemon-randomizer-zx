package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_516_Superconductor extends AbilityHackMod {
    public AbilityHackMod_516_Superconductor() {
        super(ParagonLiteAbilities.superconductor);
    }

    @Override
    public String getName(Context context) {
        return "Superconductor";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Raises Speed if hit by",
                "an Ice-type move."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Superconductor, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "is hit by an Ice-type move,",
                "its Speed goes up.",
                Dialogue.clearLine,
                "It also takes no damage or",
                "added effects from those moves."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckNoEffect3, "superconductor.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
