package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_283_GoodAsGold extends AbilityHackMod {
    public AbilityHackMod_283_GoodAsGold() {
        super(Abilities.goodAsGold);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Good as Gold";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "A body of solid gold makes\\xFFFEit immune to Status moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onAbilityCheckNoEffect, "good_as_gold_no_effect.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
