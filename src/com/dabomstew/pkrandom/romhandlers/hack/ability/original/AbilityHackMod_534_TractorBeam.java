package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_534_TractorBeam extends AbilityHackMod { 
    private final int turns;
    
    public AbilityHackMod_534_TractorBeam(int turns) {
        super(ParagonLiteAbilities.tractorBeam);
        
        this.turns = turns;
    }

    @Override
    public String getName(Context context) {
        return "Tractor Beam";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "An odd beam lifts the foe,",
                "making them easier to hit."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Tractor Beam, huh...",
                Dialogue.clearLine,
                "A Pokémon with the Ability will apply",
                "Telekinesis to the opposite Pokémon",
                "when it enters battle.",
                Dialogue.clearLine,
                "You should remember this effect lasts",
                "for three turns."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_TRACTOR_BEAM_TURNS", turns);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "tractor_beam.s"));
    }
}
