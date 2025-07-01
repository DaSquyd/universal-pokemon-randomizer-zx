package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_296_ArmorTail extends AbilityHackMod {
    public AbilityHackMod_296_ArmorTail() {
        super(Abilities.armorTail);
    }

    @Override
    public String getName(Context context, List<String> allNames) {
        return "Armor Tail";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
