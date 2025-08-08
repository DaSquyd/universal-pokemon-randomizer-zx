package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_595_MysticalFire extends MoveHackMod {
    public MoveHackMod_595_MysticalFire() {
        super(Moves.mysticalFire);
    }

    @Override
    public boolean addAnimation() {
        return true;
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return false;
    }
}
