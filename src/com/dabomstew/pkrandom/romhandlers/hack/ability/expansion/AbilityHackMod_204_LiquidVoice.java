package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_204_LiquidVoice extends AbilityHackMod {
    public AbilityHackMod_204_LiquidVoice() {
        super(Abilities.liquidVoice);
    }

    @Override
    public String getName(Context context) {
        return "Liquid Voice";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Sound-based moves",
                "become Water-type moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Liquid Voice, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "uses a sound-based move,",
                "it becomes Water-type."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "liquid_voice.s"));
    }
}
