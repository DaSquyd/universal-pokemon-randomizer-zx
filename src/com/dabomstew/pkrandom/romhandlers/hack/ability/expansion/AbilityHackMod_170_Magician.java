package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_170_Magician extends AbilityHackMod {
    public AbilityHackMod_170_Magician() {
        super(Abilities.magician);
    }

    @Override
    public String getName(Context context) {
        return "Magician";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "It steals the held item of",
                "Pokémon it hits with moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_Hit4, "magician.s"));

        return true;
    }
}
