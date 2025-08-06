package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_276_RockyPayload extends AbilityHackMod {
    public AbilityHackMod_276_RockyPayload() {
        super(Abilities.rockyPayload);
    }

    @Override
    public String getName(Context context) {
        return "Rocky Payload";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up Electric-type",
                "attacks."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Rocky Payload, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "Rock-type moves."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "rocky_payload.s"));

        return true;
    }
}
