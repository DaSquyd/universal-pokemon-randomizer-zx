package com.dabomstew.pkrandom.romhandlers.hack.ability.custom;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_053_Pickup extends AbilityHackMod {
    public AbilityHackMod_053_Pickup() {
        super(Abilities.pickup);
    }

    @Override
    public String getName(Context context) {
        return "Pickup";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "The Pokémon cleans",
                "away field hazards."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Pickup, huh...",
                Dialogue.clearLine,
                "Pokémon with this Ability now remove",
                "entry hazards like Stealth Rock on their",
                "side of the field on switch-in.",
                Dialogue.clearLine,
                "What's more...",
                Dialogue.clearLine,
                "Pokémon with this ability can sometimes",
                "find items when walking around without",
                "a held item. The higher the Pokémon's level,",
                "the better the item it can pick up."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onSwitchIn, "pickup_redux.s"));
    }
}
