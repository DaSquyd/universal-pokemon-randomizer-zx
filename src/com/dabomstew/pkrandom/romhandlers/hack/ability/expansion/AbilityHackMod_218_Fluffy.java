package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_218_Fluffy extends AbilityHackMod {
    public AbilityHackMod_218_Fluffy() {
        super(Abilities.fluffy);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Fluffy";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Halves physical damage.\\xFFFEMakes the user flamable.";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessing2, "fluffy.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
