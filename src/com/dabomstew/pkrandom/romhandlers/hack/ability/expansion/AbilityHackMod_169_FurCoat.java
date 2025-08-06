package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_169_FurCoat extends AbilityHackMod {
    double multiplier;

    public AbilityHackMod_169_FurCoat() {
        super(Abilities.furCoat);

        this.multiplier = 1.5;
    }

    public AbilityHackMod_169_FurCoat(double modifier) {
        super(Abilities.furCoat);

        this.multiplier = modifier;
    }

    @Override
    public String getName(Context context) {
        return "Fur Coat";
    }

    @Override
    public AbilityDescription getDescription(Context context) {
        if (multiplier == 2.0)
            return new AbilityDescription(
                    "Halves damage from",
                    "physical moves."
            );

        return new AbilityDescription(
                "Reduces damage from",
                "physical moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_FUR_COAT_MULTIPLIER", multiplier);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStatValue, "fur_coat.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
