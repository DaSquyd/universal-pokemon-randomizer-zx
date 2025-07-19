package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_282_QuarkDrive extends AbilityHackMod {
    public AbilityHackMod_282_QuarkDrive() {
        super(Abilities.quarkDrive);
    }

    @Override
    public String getName(Context context) {
        return "Quark Drive";
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO
    }

    @Override
    public Boolean isSkillSwapFail() {
        return true;
    }
}
