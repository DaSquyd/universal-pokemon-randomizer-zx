package com.dabomstew.pkrandom.romhandlers.hack.ability.modern;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;

import java.util.List;

public class AbilityHackMod_031_LightningRod_NameUpdate extends AbilityHackMod {
    public AbilityHackMod_031_LightningRod_NameUpdate() {
        super(Abilities.lightningRod);
    }

    @Override
    public String getName(Context context) {
        return "Lightning Rod";
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Lightning Rod, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability are immune",
                "to all Electric-type moves.",
                Dialogue.clearLine,
                "That's not all!",
                Dialogue.clearLine,
                "Its Sp. Atk goes up every time it",
                "takes an Electric-type move.",
                Dialogue.clearLine,
                "There's more!",
                Dialogue.clearLine,
                "This Ability draws Electric-type moves",
                "to the Pokémon.",
                Dialogue.clearLine,
                "It doesn't do any good in a Single Battle,",
                "but it should be useful in Double Battles",
                "and Triple Battles."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
    }
}
