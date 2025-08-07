package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_537_Dynamo_Charge extends AbilityHackMod {
    public AbilityHackMod_537_Dynamo_Charge() {
        super(ParagonLiteAbilities.dynamo);
    }

    @Override
    public String getName(Context context) {
        return "Dynamo";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Rolling and spinning moves",
                "charge the user on hit."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Dynamo, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "lands a spinning or rolling move,",
                "the user becomes charged."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_HitReal, "dynamo_charge.s"));
    }
}
