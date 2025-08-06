package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_527_WarpDrive extends AbilityHackMod {
    public AbilityHackMod_527_WarpDrive() {
        super(ParagonLiteAbilities.warpDrive);
    }

    @Override
    public String getName(Context context) {
        return "Warp Drive";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Doubles the Pokémon's",
                "Speed on Psychic Terrain."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Warp Drive, huh...",
                Dialogue.clearLine,
                "This Ability doubles a Pokémon's",
                "Speed on Psychic Terrain."
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }
}
