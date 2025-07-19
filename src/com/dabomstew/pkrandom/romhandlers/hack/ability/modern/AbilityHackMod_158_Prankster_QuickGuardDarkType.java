package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_158_Prankster_QuickGuardDarkType extends AbilityHackMod {
    public AbilityHackMod_158_Prankster_QuickGuardDarkType() {
        super(Abilities.prankster);
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePriority, "prankster.s"));
    }
}
