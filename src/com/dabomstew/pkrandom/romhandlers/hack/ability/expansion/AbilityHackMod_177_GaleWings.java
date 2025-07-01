package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_177_GaleWings extends AbilityHackMod {
    public enum HPRequirement {
        None(0),
        Half(2),
        Full(1);
        
        final int hpFraction;
        
        HPRequirement(int hpFraction) {
            this.hpFraction = hpFraction;
        }
    }
    
    private final HPRequirement hpRequirement;

    public AbilityHackMod_177_GaleWings() {
        super(Abilities.galeWings);

        this.hpRequirement = HPRequirement.Full;
    }

    public AbilityHackMod_177_GaleWings(HPRequirement hpRequirement) {
        super(Abilities.galeWings);

        this.hpRequirement = hpRequirement;
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Gale Wings";
    }

    @Override
    public String getDescription(Context context, List<String> allDescriptions) {
        switch (hpRequirement) {
            case None -> {
                return "Gives priority to\\xFFFEFlying-type moves.";
            }
            case Half -> {
                return "Gives priority to Flying-\\xFFFEtype moves at high HP";
            }
            case Full -> {
                return "Gives priority to Flying-\\xFFFEtype moves at full HP";
            }
            default -> throw new IllegalStateException("Unexpected value: " + hpRequirement);
        }
    }

    @Override
    public String getExplanation(Context context, List<String> allExplanations) {
        // TODO
        return super.getExplanation(context, allExplanations);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of("ABILITY_GALE_WINGS_HP_FRACTION", hpRequirement.hpFraction);
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePriority, "gale_wings.s"));
    }
}
