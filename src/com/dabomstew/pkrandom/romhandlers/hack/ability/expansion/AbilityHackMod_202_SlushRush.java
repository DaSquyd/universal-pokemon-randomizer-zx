package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_202_SlushRush extends AbilityHackMod {
    public AbilityHackMod_202_SlushRush() {
        super(Abilities.slushRush);
    }

    @Override
    public String getName(Context context) {
        return "Slush Rush";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Doubles the Pokémon's",
                "Speed in a Hailstorm."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Slush Rush, huh...",
                Dialogue.clearLine,
                "This Ability doubles a Pokémon's",
                "Speed when it's hailing."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCalcSpeed, "slush_rush_speed.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onWeatherReaction, "slush_rush_weather_immune.s"));

        return true;
    }
}
