package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

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
    public String getName(Context context) {
        return "Gale Wings";
    }

    @Override
    public GameText getDescription(Context context) {
        switch (hpRequirement) {
            case None -> {
                return new AbilityDescription("Gives priority to", "Flying-type moves.");
            }
            case Half -> {
                return new AbilityDescription("Gives priority to Flying-", "type moves at high HP.");
            }
            case Full -> {
                return new AbilityDescription("Gives priority to Flying-", "type moves at full HP.");
            }
            default -> throw new IllegalStateException("Unexpected value: " + hpRequirement);
        }
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
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
