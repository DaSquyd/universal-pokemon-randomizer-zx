package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_508_Undercurrent extends AbilityHackMod {
    public AbilityHackMod_508_Undercurrent() {
        super(ParagonLiteAbilities.undercurrent);
    }

    @Override
    public String getName(Context context) {
        return "Undercurrent";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts the Speed stat",
                "of the Pokémon's allies."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Undercurrent, huh...",
                Dialogue.clearLine,
                "A Pokémon with this Ability",
                "raises the Speed",
                "of itself and its allies."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "undercurrent_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onRotateIn, "undercurrent_message.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCalcSpeed, "undercurrent_speed.s"));
    }
}
