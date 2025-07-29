package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_126_Contrary extends AbilityHackMod {
    public AbilityHackMod_126_Contrary() {
        super(Abilities.contrary);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Flips stat changes to have",
                "the opposite effect."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Contrary, huh...",
                Dialogue.clearLine,
                "Stat changes have the opposite effect",
                "on Pokémon with this Ability!",
                Dialogue.clearLine,
                "When stats should go up, they go down,",
                "and when they should go down, they go up!"
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChange, "contrary.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
