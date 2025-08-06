package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_542_FlutterDust extends AbilityHackMod {
    public AbilityHackMod_542_FlutterDust() {
        super(ParagonLiteAbilities.flutterDust);
    }

    @Override
    public String getName(Context context) {
        return "Flutter Dust";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Moves always inflict poison",
                "when HP is half or less."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Flutter Dust, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "lands a hit while its HP is half or less,",
                "the target is poisoned."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "flutter_dust.s"));

        return true;
    }
}
