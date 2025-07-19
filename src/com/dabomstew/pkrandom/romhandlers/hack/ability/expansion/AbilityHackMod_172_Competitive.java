package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_172_Competitive extends AbilityHackMod {
    public AbilityHackMod_172_Competitive() {
        super(Abilities.competitive);
    }

    @Override
    public String getName(Context context) {
        return "Competitive";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "If a stat is lowered,",
                "Sp. Atk sharply increases."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Competitive, huh...",
                Dialogue.clearLine,
                "When an opponent lowers the stats",
                "of a Pokémon with this Ability,",
                "its Sp. Atk goes way up!",
                Dialogue.clearLine,
                "But be careful--if it lowers",
                "its own stats, the Ability won't work!"
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeSuccess, "competitive.s"));
    }
}
