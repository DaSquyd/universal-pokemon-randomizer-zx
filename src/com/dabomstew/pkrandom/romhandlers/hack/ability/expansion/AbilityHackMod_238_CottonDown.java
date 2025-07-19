package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_238_CottonDown extends AbilityHackMod {
    private final boolean onlyFoes;
    
    public AbilityHackMod_238_CottonDown() {
        super(Abilities.cottonDown);
        
        this.onlyFoes = false;
    }
    
    public AbilityHackMod_238_CottonDown(boolean onlyFoes) {
        super(Abilities.cottonDown);
        
        this.onlyFoes = onlyFoes;
    }

    @Override
    public String getName(Context context) {
        return "Cotton Down";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Cotton fluff scatters on",
                "contact, slowing others."
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_COTTON_DOWN_ONLY_FOES", onlyFoes);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }
}
