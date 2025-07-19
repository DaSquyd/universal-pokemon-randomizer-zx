package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_299_Hospitality extends AbilityHackMod {    
    public AbilityHackMod_299_Hospitality() {
        super(Abilities.hospitality);
    }

    @Override
    public String getName(Context context) {
        return "Hospitality";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Restores a small amount",
                "of ally HP on entry."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "hospitality.s"));
    }
}
