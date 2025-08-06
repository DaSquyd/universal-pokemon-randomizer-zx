package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_295_ToxicDebris extends AbilityHackMod {    
    public AbilityHackMod_295_ToxicDebris() {
        super(Abilities.toxicDebris);
    }

    @Override
    public String getName(Context context) {
        return "Toxic Debris";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Scatters poison spikes if",
                "physical damage is taken."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }
}
