package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.romhandlers.hack.Ability.AbilityIlluminateMode;
import com.dabomstew.pkrandom.romhandlers.hack.Ability.AbilityRattledMode;
import com.dabomstew.pkrandom.romhandlers.hack.Weather.WeatherHailSnowMode;

public class ModernHackMode extends HackMode {
    public ModernHackMode() {
        super("Modern");
    }

    public ModernHackMode(String name) {
        super(name);
    }

    @Override
    protected void setValues() {
        // Ability
        abilityObliviousIgnoresTaunt = true; // #012
        abilityObliviousIgnoresIntimidate = true; // #012
        abilityCompoundEyesName = "Compound Eyes"; // #014
        abilityLightningRodName = "Lightning Rod"; // #031
        abilityIlluminateMode = AbilityIlluminateMode.MODERN; // #035
        abilityInnerFocusIgnoresIntimidate = false; // #039
        abilitySoundproofIsImmuneToPerishSong = true; // #043
        abilityRattledMode = AbilityRattledMode.MODERN; // #155

        // Moves
        trapMoveDamageFraction = 8;
        trapMoveDamageFractionWithBoost = 6;
        screenMoveDoubleBattleReduction = 0.667;

        // Items
        gemItemDamageMultiplier = 1.3;

        // Status
        statusBurnDamageFraction = 16;
        statusParalysisSpeedPercent = 50;
        statusSleepResetTurnsOnSwitch = false;
        statusConfusionHitPercent = 33;

        // Type
        typeGhostCanAlwaysEscape = true;
        typeGrassIsImmuneToPowderMoves = true;
        typeDarkIsImmuneToPrankster = true;
        
        // Weather
        weatherHailSnowMode = WeatherHailSnowMode.MODERN_SNOW;

        // Pokémon Data
        pokemonData = new PokemonDataIni("gen9pokes.ini", true);
        pokemonDataExpYieldScale = new double[]{0.2, 0.35, 0.5};
        
        // Misc
        shinyRate = 4096;
        criticalHitMultiplier = 1.5;
        criticalHitRatios = new byte[]{24, 8, 2, 1};
        dynamicTurnOrder = true;
    }
}
