package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_145_BigPecks_OnStatDropRaiseDefense extends AbilityHackMod {
    public AbilityHackMod_145_BigPecks_OnStatDropRaiseDefense() {
        super(Abilities.bigPecks);
    }

    @Override
    public String getName(Context context) {
        return "Big Pecks";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "If a stat falls, Defense",
                "sharply increases."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Big Pecks, huh...",
                Dialogue.clearLine,
                "When an opponent lowers the stats",
                "of a Pokémon with this Ability,",
                "its Defense goes way up!",
                Dialogue.clearLine,
                "But be careful--if it lowers",
                "its own stats, the Ability won't work!"
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveStatStageChangeApplied, "big_pecks.s"));
    }
}
