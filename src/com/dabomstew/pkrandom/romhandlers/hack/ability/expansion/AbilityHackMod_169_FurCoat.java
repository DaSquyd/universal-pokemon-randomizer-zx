package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.ParagonLiteHandler;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

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
    public String getName(Context context, List<String> allNames) {
        return "Fur Coat";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        return "Halves the damage\\xFFFEfrom physical moves.";
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_FUR_COAT_MULTIPLIER", multiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetDefendingStatValue, "fur_coat.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
