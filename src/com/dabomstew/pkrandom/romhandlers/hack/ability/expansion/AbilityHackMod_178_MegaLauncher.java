package com.dabomstew.pkrandom.romhandlers.hack.ability.expansion;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Gen5BattleEventType;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.string.AbilityDescription;
import com.dabomstew.pkrandom.romhandlers.hack.string.Dialogue;
import com.dabomstew.pkrandom.romhandlers.hack.string.GameText;

import java.util.List;
import java.util.Map;

public class AbilityHackMod_178_MegaLauncher extends AbilityHackMod {
    private final double multiplier;
    private final boolean includesBallBombMoves;
    
    public AbilityHackMod_178_MegaLauncher() {
        super(Abilities.megaLauncher);

        this.multiplier = 1.5;
        this.includesBallBombMoves = false;
    }

    public AbilityHackMod_178_MegaLauncher(double multiplier) {
        super(Abilities.megaLauncher);

        this.multiplier = multiplier;
        this.includesBallBombMoves = false;
    }
    
    public AbilityHackMod_178_MegaLauncher(double multiplier, boolean includesBallBombMoves) {
        super(Abilities.megaLauncher);

        this.multiplier = multiplier;
        this.includesBallBombMoves = includesBallBombMoves;
    }

    @Override
    public String getName(Context context) {
        return "Mega Launcher";
    }

    @Override
    public GameText getDescription(Context context) {
        return new AbilityDescription("Powers up aura and", "pulse moves.");
    }

    @Override
    public Dialogue getExplanation(Context context) {
        // TODO
        return super.getExplanation(context);
    }

    @Override
    public Map<String, Object> getGlobalValues(Context context) {
        return Map.of(
                "ABILITY_MEGA_LAUNCHER_MULTIPLIER", multiplier,
                "ABILITY_MEGA_LAUNCHER_INCLUDES_BALL_BOMB_MOVES", includesBallBombMoves
        );
    }

    @Override
    public void populateQueueEntries(Context context, List<QueueEntry> inOutQueueEntries) {
        inOutQueueEntries.add(new QueueEntry(Gen5BattleEventType.onGetMovePower, "mega_launcher.s"));
    }
}
