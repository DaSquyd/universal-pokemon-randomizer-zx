package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_538_RabbitsFoot extends AbilityHackMod {
    public AbilityHackMod_538_RabbitsFoot() {
        super(ParagonLiteAbilities.rabbitsFoot);
    }

    @Override
    public String getName(Context context) {
        return "Rabbit's Foot";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "The Pokémon's kicking",
                "moves never miss."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Rabbit's Foot, huh...",
                Dialogue.clearLine,
                "A Pokémon with this Ability cannot miss",
                "their kicking moves, unless the target is",
                "semi-invulnerable."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onBypassAccuracyCheck, "rabbits_foot.s"));

        return true;
    }
}
