package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_510_Glazeware extends AbilityHackMod {
    public AbilityHackMod_510_Glazeware() {
        super(ParagonLiteAbilities.glazeware);
    }

    @Override
    public String getName(Context context) {
        return "Glazeware";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Resistant to Water- and",
                "Poison-type moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Glazeware, huh...",
                Dialogue.clearLine,
                "This Ability halves damage from",
                "Water- and Poison-type moves."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "glazeware.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
