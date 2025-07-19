package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_534_TractorBeam extends AbilityHackMod {    
    public AbilityHackMod_534_TractorBeam() {
        super(ParagonLiteAbilities.tractorBeam);
    }

    @Override
    public String getName(Context context) {
        return "Tractor Beam";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Moves with recoil boost",
                "the Pokémon's Speed stat."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onDamageProcessingEnd_HitReal, "tenacity.s"));
    }
}
