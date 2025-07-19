package com.dabomstew.pkrandom.romhandlers.hack.ability.original;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.ParagonLiteAbilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;

public class AbilityHackMod_504_SpringLegs extends AbilityHackMod {
    private final String name;

    public AbilityHackMod_504_SpringLegs() {
        super(ParagonLiteAbilities.springLegs);

        name = "Spring Legs";
    }

    public AbilityHackMod_504_SpringLegs(String name) {
        super(ParagonLiteAbilities.springLegs);
        
        this.name = name;
    }

    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription(
                "Powers up kicking moves.",
                ""
        );
    }

    @Override
    public Dialogue getExplanation(Context context) {
        return new Dialogue(
                name + ", huh...",
                Dialogue.clearLine,
                "This Ability increases the power of",
                "moves that kick the target."
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "spring_legs.s"));
    }
}
