package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_199_WaterBubble extends AbilityHackMod {
    public AbilityHackMod_199_WaterBubble() {
        super(Abilities.waterBubble);
    }

    @Override
    public String getName(Context context) {
        return "Water Bubble";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Lowers the power of", "Fire-type moves.");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Water Bubble, huh...",
                Dialogue.clearLine,
                "This Ability halves damage",
                "from Fire-type moves.",
                Dialogue.clearLine,
                "It also doubles the power",
                "of Water-type moves.",
                Dialogue.clearLine,
                "You should remember that",
                "it also prevents the burned",
                "status condition."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
