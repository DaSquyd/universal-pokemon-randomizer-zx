package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_205_Triage extends AbilityHackMod {
    public AbilityHackMod_205_Triage() {
        super(Abilities.triage);
    }

    @Override
    public String getName(Context context) {
        return "Triage";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Gives priority to",
                "a healing move."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Triage, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability can",
                "use healing and HP-draining moves",
                "earlier than usual."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePriority, "triage.s"));
    }
}
