package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_124_Pickpocket_OnAttack extends AbilityHackMod {
    public AbilityHackMod_124_Pickpocket_OnAttack() {
        super(Abilities.pickpocket);
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Steals held items when",
                "contact is made."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Pickpocket, huh...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "is hit with a direct attack,",
                "it can steal the attacker's held item.",
                Dialogue.clearLine,
                "What's more...",
                Dialogue.clearLine,
                "When a Pokémon with this Ability",
                "lands a direct attack,",
                "it can steal the target's held item.",
                Dialogue.clearLine,
                "Well...",
                Dialogue.clearLine,
                "It only works if the opposing Pokémon",
                "is holding an item, though."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_Hit4, "pickpocket.s"));
    }
}
