package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_080_Steadfast_TauntImmunity extends AbilityHackMod {
    public AbilityHackMod_080_Steadfast_TauntImmunity() {
        super(Abilities.steadfast);
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Steadfast, huh...",
                Dialogue.clearLine,
                "This Ability raises the Pokémon's Speed",
                "every time it flinches.",
                Dialogue.clearLine,
                "What's more...",
                Dialogue.clearLine,
                "It lowers the chance to encounter",
                "wild Pokémon when the leading party",
                "member has it."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveExecuteFail));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onCheckNoEffect3, "steadfast_taunt.s"));

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
