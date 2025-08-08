package com.dabomstew.pkrandom.romhandlers.hack.move.expansion;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_574_DisarmingVoice extends MoveHackMod {
    public MoveHackMod_574_DisarmingVoice() {
        super(Moves.disarmingVoice);
    }

    @Override
    public boolean addAnimation() {
        return true;
    }

    @Override
    public int[] getAnimationSpaFiles() {
        return new int[]{743};
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        return false;
    }
}
