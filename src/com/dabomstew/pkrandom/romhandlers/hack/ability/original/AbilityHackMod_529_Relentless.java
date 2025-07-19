package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_529_Relentless extends AbilityHackMod {
    private final String name;
    
    public AbilityHackMod_529_Relentless() {
        super(ParagonLiteAbilities.adrenalineRush);

        this.name = "Relentless";
    }
    
    public AbilityHackMod_529_Relentless(String name) {
        super(ParagonLiteAbilities.adrenalineRush);

        this.name = name;
    }

    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Does not need to recharge",
                "if the target faints."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                name + ", huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "knocks out an opponent,",
                "it never needs to rest on the next turn."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }
}
