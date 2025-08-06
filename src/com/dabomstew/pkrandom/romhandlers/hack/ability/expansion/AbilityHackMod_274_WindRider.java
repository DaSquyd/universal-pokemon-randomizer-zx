package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_274_WindRider extends AbilityHackMod {
    public AbilityHackMod_274_WindRider() {
        super(Abilities.windRider);
    }

    @Override
    public String getName(Context context) {
        return "Wind Rider";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Boosts Attack when hit by", "a wind move.");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Wind Rider, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "is hit by a wind move,",
                "its Attack goes up.",
                Dialogue.clearLine,
                "It also takes no damage or",
                "added effects from those moves.",
                Dialogue.clearLine,
                "You should remember that",
                "Attack will also go up",
                "if Tailwind takes effect."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckNoEffect3, "wind_rider_immunity.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "wind_rider_on_enter.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onPostAbilityChange, "wind_rider_on_enter.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteEffective, "wind_rider_after_tailwind.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
