package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_072_VitalSpirit_SpDef extends AbilityHackMod {
    public AbilityHackMod_072_VitalSpirit_SpDef() {
        super(Abilities.vitalSpirit);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts the Sp. Def stat",
                "when hit by an attack."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "vital_spirit.s"));
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
