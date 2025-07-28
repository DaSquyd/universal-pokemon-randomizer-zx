package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_297_EarthEater extends AbilityHackMod {
    public AbilityHackMod_297_EarthEater() {
        super(Abilities.earthEater);
    }

    @Override
    public String getName(Context context) {
        return "Earth Eater";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Restores HP if hit by a",
                "Ground-type move."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Earth Eater, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability do not",
                "take damage from Ground-type moves",
                "Their HP is restored a little instead."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "earth_eater.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
