package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.newgui.NewRandomizerGUI;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_518_Incendiate extends AbilityHackMod {
    public AbilityHackMod_518_Incendiate() {
        super(ParagonLiteAbilities.incendiate);
    }

    @Override
    public String getName(Context context) {
        return "Incendiate";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Normal-type moves become",
                "Fire-type moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
         return new Dialogue(
                 "Incendiate, huh...",
                 Dialogue.clearLine,
                 "When a Pokémon with this Ability",
                 "uses a Normal-type attack,",
                 "it becomes Fire-type instead.",
                 Dialogue.clearLine,
                 "There's more!",
                 Dialogue.clearLine,
                 "Attacks that become Fire-type",
                 "also become powered up!",
                 Dialogue.clearLine,
                 "You should remember that it has",
                 "no effect on moves that can",
                 "change type on their own."
         );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "incendiate_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "incendiate_power.s"));
    }
}
