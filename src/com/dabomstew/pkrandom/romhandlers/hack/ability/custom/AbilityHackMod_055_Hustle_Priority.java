package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_055_Hustle_Priority extends AbilityHackMod {
    public AbilityHackMod_055_Hustle_Priority(int percentChance) {
        super(Abilities.hustle);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Gives priority to the",
                "Pokémon's weaker moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Hustle, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability can",
                "use low-power moves earlier",
                "than usual.",
                Dialogue.clearLine,
                "This has no effect on moves with",
                "multiple hits or always score critical hits.",
                "You should remember that.",
                Dialogue.clearLine,
                "What's more...",
                Dialogue.clearLine,
                "It raises the chance to encounter",
                "high-level wild Pokémon",
                "when the leading party member has it."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePriority, "hustle_priority.s"));

        return true;
    }
}
