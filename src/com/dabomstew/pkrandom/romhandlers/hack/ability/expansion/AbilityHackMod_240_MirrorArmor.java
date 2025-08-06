package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_240_MirrorArmor extends AbilityHackMod {
    public AbilityHackMod_240_MirrorArmor() {
        super(Abilities.mirrorArmor);
    }

    @Override
    public String getName(Context context) {
        return "Mirror Armor";
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }

    @Override
    public Boolean isBreakable() {
        return true;
    }
}
