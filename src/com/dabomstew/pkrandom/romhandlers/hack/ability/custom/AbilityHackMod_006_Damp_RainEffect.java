package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AbilityHackMod_006_Damp_RainEffect extends AbilityHackMod {
    public AbilityHackMod_006_Damp_RainEffect() {
        super(Abilities.damp);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Collections.singletonMap("ABILITY_DAMP_IS_RAIN_EFFECT", true);
    }

    @Override
    public String getDescription(Context context, List<String> abilityDescriptions) {
        return "All moves have the\\xFFFE" +
                "effects of rain.";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, Abilities.heatproof));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onConditionDamage, Abilities.heatproof));
        
        // full effect in get_effective_weather.s
    }
}
