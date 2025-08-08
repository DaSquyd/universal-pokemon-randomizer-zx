package com.dabomstew.pkrandom.romhandlers.hack.move.modern;

import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.romhandlers.hack.MoveHackMod;

import java.util.List;

public class MoveHackMod_311_WeatherBall extends MoveHackMod {
    public MoveHackMod_311_WeatherBall() {
        super(Moves.weatherBall);
    }

    @Override
    public boolean registerEventHandlers(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveParam, "weather_ball_type.s"));
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMoveBasePower, "weather_ball_base_power.s"));
        
        return true;
    }
}
