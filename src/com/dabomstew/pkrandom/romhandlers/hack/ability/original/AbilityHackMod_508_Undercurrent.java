package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_508_Undercurrent extends AbilityHackMod {
    public AbilityHackMod_508_Undercurrent() {
        super(ParagonLiteAbilities.undercurrent);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Undercurrent";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Boosts the Speed stat\\xFFFEof the Pokémon's allies.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        return allExplanations.get(Abilities.victoryStar)
                .replace("Victory Star", "Undercurrent")
                .replace("accuracy", "Speed stat");
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "undercurrent_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "undercurrent_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCalcSpeed, "undercurrent_speed.s"));
    }
}
