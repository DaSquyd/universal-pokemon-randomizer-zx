package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_502_Insectivore extends AbilityHackMod {
    public AbilityHackMod_502_Insectivore() {
        super(ParagonLiteAbilities.insectivore);
    }

    @Override
    public String getName(Context context) {
        return "Insectivore";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Restores HP if hit by a",
                "Bug-type move."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Insectivore, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability do not",
                "take damage from Bug-type moves",
                "Their HP is restored a little instead."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckNoEffect3, "insectivore.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
