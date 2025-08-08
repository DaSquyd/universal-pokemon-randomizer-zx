package com.dabomstew.pkrandom.romhandlers.hack.move.custom;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;
import java.util.Map;

public class MoveHackMod_167_TripleKick extends MoveHackMod {
    private final int increment;
    
    public MoveHackMod_167_TripleKick(int increment) {
        super(Moves.tripleKick);
        
        this.increment = increment;
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "MOVE_TRIPLE_KICK_BASE_POWER", context.moves.get(Moves.tripleKick).power,
                "MOVE_TRIPLE_KICK_INCREMENT", increment
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveBasePower, "triple_kick.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
        
        return true;
    }
}
