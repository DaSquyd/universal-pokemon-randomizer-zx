package com.dabomstew.pkrandom.romhandlers.hack.Ability;

public enum AbilityTruantMode {
    VANILLA(false, false, false),
    VANILLA_PLUS_HEAL_ON_LOAFING_AROUND_TURNS(false, true, false),
    VANILLA_PLUS_HEAL_ON_LOAFING_AROUND_TURNS_PLUS_IGNORE_FAILED_MOVES(false, true, true),
    VANILLA_PLUS_HEAL_EVERY_TURN(true, true, false),
    VANILLA_PLUS_HEAL_EVERY_TURN_PLUS_IGNORE_FAILED_MOVES(true, true, true);

    public final boolean healOnMoveTurns;
    public final boolean healOnLoafingTurns;
    public final boolean ignoreFailedMoves;

    AbilityTruantMode(boolean healOnMoveTurns, boolean healOnLoafingTurns, boolean ignoreFailedMoves) {
        this.healOnMoveTurns = healOnMoveTurns;
        this.healOnLoafingTurns = healOnLoafingTurns;
        this.ignoreFailedMoves = ignoreFailedMoves;
    }
}
