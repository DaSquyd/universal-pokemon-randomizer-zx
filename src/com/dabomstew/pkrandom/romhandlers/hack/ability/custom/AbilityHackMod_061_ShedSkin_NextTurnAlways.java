package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_061_ShedSkin_NextTurnAlways extends AbilityHackMod {
    public AbilityHackMod_061_ShedSkin_NextTurnAlways() {
        super(Abilities.shedSkin);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "The Pokémon heals its",
                "own status problems."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Shed Skin, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability cure their",
                "status conditions at the start of the turn."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckSpecialPriority, "shed_skin.s"));
    }
}
