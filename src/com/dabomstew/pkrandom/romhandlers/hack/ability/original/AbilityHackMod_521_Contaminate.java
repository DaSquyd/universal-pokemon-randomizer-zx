package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_521_Contaminate extends AbilityHackMod {
    public AbilityHackMod_521_Contaminate() {
        super(ParagonLiteAbilities.contaminate);
    }

    @Override
    public String getName(Context context) {
        return "Contaminate";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Normal-type moves become",
                "Poison-type moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Contaminate, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "uses a Normal-type attack,",
                "it becomes Poison-type instead.",
                Dialogue.clearLine,
                "There's more!",
                Dialogue.clearLine,
                "Attacks that become Poison-type",
                "also become powered up!",
                Dialogue.clearLine,
                "You should remember that it has",
                "no effect on moves that can",
                "change type on their own."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "contaminate_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "contaminate_power.s"));

        return true;
    }
}
