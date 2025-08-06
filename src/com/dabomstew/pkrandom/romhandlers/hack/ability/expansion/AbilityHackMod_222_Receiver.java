package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;

import java.util.List;

public class AbilityHackMod_222_Receiver extends AbilityHackMod {
    public AbilityHackMod_222_Receiver() {
        super(Abilities.receiver);
    }

    @Override
    public String getName(Context context) {
        return "Receiver";
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        // TODO

        return true;
    }

    @Override
    public Boolean isRolePlayFail() {
        return true;
    }
}
