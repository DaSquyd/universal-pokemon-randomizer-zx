package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_263_DragonsMaw extends AbilityHackMod {
    public AbilityHackMod_263_DragonsMaw() {
        super(Abilities.dragonsMaw);
    }

    @Override
    public String getName(Context context) {
        return "Dragon's Maw";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up Dragon-type",
                "attacks."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Dragon's Maw, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "Dragon-type moves."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetAttackingStatValue, "dragons_maw.s"));

        return true;
    }
}
