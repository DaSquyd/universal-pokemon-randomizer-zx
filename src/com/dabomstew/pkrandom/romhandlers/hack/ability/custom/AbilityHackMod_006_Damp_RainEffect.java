package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AbilityHackMod_006_Damp_RainEffect extends AbilityHackMod {
    public AbilityHackMod_006_Damp_RainEffect() {
        super(Abilities.damp);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("The user feels a mist", "of rain in any weather.");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Collections.singletonMap("ABILITY_DAMP_IS_RAIN_EFFECT", true);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, Abilities.heatproof));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onConditionDamage, Abilities.heatproof));

        // full effect in get_effective_weather.s
    }
}
