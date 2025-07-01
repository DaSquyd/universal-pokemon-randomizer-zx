package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_171_Bulletproof extends AbilityHackMod {
    public AbilityHackMod_171_Bulletproof() {
        super(Abilities.bulletproof);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Bulletproof";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Protects the Pokémon from\\xFFFEsome ball and bomb moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "bulletproof.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
