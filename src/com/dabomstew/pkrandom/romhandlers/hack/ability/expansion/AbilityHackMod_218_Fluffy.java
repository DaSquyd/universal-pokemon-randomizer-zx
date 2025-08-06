package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_218_Fluffy extends AbilityHackMod {
    public AbilityHackMod_218_Fluffy() {
        super(Abilities.fluffy);
    }

    @Override
    public String getName(Context context) {
        return "Fluffy";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Halves physical damage.", "Makes the user flammable.");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageProcessing2, "fluffy.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
