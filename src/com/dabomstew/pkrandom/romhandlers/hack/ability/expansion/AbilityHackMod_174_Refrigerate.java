package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_174_Refrigerate extends AbilityHackMod {
    public AbilityHackMod_174_Refrigerate() {
        super(Abilities.refrigerate);
    }

    @Override
    public String getName(Context context) {
        return "Refrigerate";
    }
    
    @Override
    public AbilityDescription getDescription(Context context) {
        return new AbilityDescription(
                "Normal-type moves become",
                "Ice-type moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Refrigerate, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "uses a Normal-type attack,",
                "it becomes Ice-type instead.",
                Dialogue.clearLine,
                "There's more!",
                Dialogue.clearLine,
                "Attacks that become Ice-type also",
                "become powered up!",
                Dialogue.clearLine,
                "You should remember that it has",
                "no effect on moves that can",
                "change type on their own."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "refrigerate_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "refrigerate_power.s"));

        return true;
    }
}
