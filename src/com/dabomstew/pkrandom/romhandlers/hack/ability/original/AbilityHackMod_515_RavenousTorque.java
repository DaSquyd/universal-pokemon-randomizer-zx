package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_515_RavenousTorque extends AbilityHackMod {
    public AbilityHackMod_515_RavenousTorque() {
        super(ParagonLiteAbilities.ravenousTorque);
    }

    @Override
    public String getName(Context context) {
        return "Ravenous Torque";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Twisting boosts the user's",
                "Speed after a biting move."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                "Ravenous Torque, huh...",
                Dialogue.clearLine,
                "This Ability raises a Pokémon's",
                "Speed if it lands",
                "a move that bites the target."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "ravenous_torque_power.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_HitReal, "ravenous_torque_speed.s"));
    }
}
