package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_514_Capoeirista extends AbilityHackMod {
    public AbilityHackMod_514_Capoeirista() {
        super(ParagonLiteAbilities.capoeirista);
    }

    @Override
    public String getName(Context context) {
        return "Capoeirista";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Its ginga footwork powers",
                "up spin and roll moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Capoeirista, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "moves that spin or roll."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "capoeirista.s"));

        return true;
    }
}
