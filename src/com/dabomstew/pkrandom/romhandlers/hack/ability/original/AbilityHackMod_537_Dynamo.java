package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_537_Dynamo extends AbilityHackMod {
    private final double multiplier;

    public AbilityHackMod_537_Dynamo() {
        super(ParagonLiteAbilities.dynamo);

        this.multiplier = 1.3;
    }

    public AbilityHackMod_537_Dynamo(double multiplier) {
        super(ParagonLiteAbilities.dynamo);

        this.multiplier = multiplier;
    }

    @Override
    public String getName(Context context) {
        return "Ravenous Torque";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Twisting boosts the user's",
                "Speed after a biting move."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Ravenous Torque, huh...",
                Dialogue.clearLine,
                "This Ability raises a Pokémon's",
                "Speed if it lands",
                "a move that bites the target."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_DYNAMO_MULTIPLIER", multiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "dynamo_power.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_HitReal, "dynamo_speed.s"));
    }
}
