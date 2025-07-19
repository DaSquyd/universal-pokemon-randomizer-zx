package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_283_GoodAsGold extends AbilityHackMod {
    public AbilityHackMod_283_GoodAsGold() {
        super(Abilities.goodAsGold);
    }

    @Override
    public String getName(Context context) {
        return "Good as Gold";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "A solid gold body gives it",
                "immunity to Status moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
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
