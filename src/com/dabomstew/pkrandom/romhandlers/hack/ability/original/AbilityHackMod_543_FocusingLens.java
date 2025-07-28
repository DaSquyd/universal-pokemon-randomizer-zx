package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_543_FocusingLens extends AbilityHackMod {
    private final double multiplier;
    private final boolean includeBeamMoves;

    public AbilityHackMod_543_FocusingLens(double multiplier, boolean includeBeamMoves) {
        super(ParagonLiteAbilities.focusingLens);
        
        this.multiplier = multiplier;
        this.includeBeamMoves = includeBeamMoves;
    }

    @Override
    public String getName(Context context) {
        return "Focusing Lens";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Boosts the power of",
                includeBeamMoves ? "light and beam moves." : "light moves."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Focusing Lens, huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                includeBeamMoves ? "light and beam moves." : "light moves."
        );
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_FOCUSING_LENS_MULTIPLIER", multiplier);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, includeBeamMoves ? "focusing_lens_beam.s" : "focusing_lens.s"));
    }
}
