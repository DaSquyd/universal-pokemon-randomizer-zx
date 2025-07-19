package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_182_Pixilate extends AbilityHackMod {
    public AbilityHackMod_182_Pixilate() {
        super(Abilities.pixilate);
    }

    @Override
    public String getName(Context context) {
        return "Pixilate";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Normal-type moves become", "Fairy-type moves.");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Pixilate, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "uses a Normal-type attack,",
                "it becomes Fairy-type instead.",
                Dialogue.clearLine,
                "There's more!",
                Dialogue.clearLine,
                "Attacks that become Fairy-type",
                "also become powered up!",
                Dialogue.clearLine,
                "You should remember that it has",
                "no effect on moves that can",
                "change type on their own."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "pixilate_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "pixilate_power.s"));
    }
}
