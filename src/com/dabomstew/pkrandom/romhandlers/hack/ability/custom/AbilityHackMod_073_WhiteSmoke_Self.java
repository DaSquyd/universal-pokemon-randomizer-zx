package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_073_WhiteSmoke_Self extends AbilityHackMod {
    public AbilityHackMod_073_WhiteSmoke_Self() {
        super(Abilities.whiteSmoke);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Strange smoke prevents",
                "the lowering of its stats."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "White Smoke, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability are protected",
                "against stat-lowering moves and Abilities.",
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
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeLastCheck, "white_smoke.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onStatStageChangeFail));

        return true;
    }
}
