package com.dabomstew.pkrandom.romhandlers.hack.move.custom;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;
import java.util.Map;

public class MoveHackMod_360_GyroBall extends MoveHackMod {
    private final int multiplier;
    private final int maxPower;
    
    public MoveHackMod_360_GyroBall(int multiplier, int maxPower) {
        super(Moves.gyroBall);
        
        this.multiplier = multiplier; // 50
        this.maxPower = maxPower; // 150
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "MOVE_GYRO_BALL_MULTIPLIER", multiplier,
                "MOVE_GYRO_BALL_MAX_POWER", maxPower
        );
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveBasePower, "gyro_ball.s"));
        
        return true;
    }
}
