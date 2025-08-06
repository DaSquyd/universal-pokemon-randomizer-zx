package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_183_Gooey extends AbilityHackMod {
    private final int stages;

    public AbilityHackMod_183_Gooey() {
        super(Abilities.gooey);

        this.stages = 1;
    }

    public AbilityHackMod_183_Gooey(int stages) {
        super(Abilities.gooey);

        this.stages = stages;
    }

    @Override
    public String getName(Context context) {
        return "Gooey";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Thick, sticky slime harshly", "lowers Speed on contact.");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Gooey, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "is hit by a direct attack,",
                "it" + (stages > 1 ? " harshly " : " ") + "lowers the attacker's Speed."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_GOOEY_STAGES", stages);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onMoveDamageReaction1, "gooey.s"));

        return true;
    }
}
