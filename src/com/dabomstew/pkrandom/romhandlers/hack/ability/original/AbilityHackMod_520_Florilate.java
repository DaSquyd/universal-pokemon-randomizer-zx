package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_520_Florilate extends AbilityHackMod {
    public AbilityHackMod_520_Florilate() {
        super(ParagonLiteAbilities.florilate);
    }

    @Override
    public String getName(Context context) {
        return "Florilate";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Normal-type moves become",
                "Grass-type moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Florilate, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "uses a Normal-type attack,",
                "it becomes Grass-type instead.",
                Dialogue.clearLine,
                "There's more!",
                Dialogue.clearLine,
                "Attacks that become Grass-type",
                "also become powered up!",
                Dialogue.clearLine,
                "You should remember that it has",
                "no effect on moves that can",
                "change type on their own."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "florilate_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "florilate_power.s"));

        return true;
    }
}
