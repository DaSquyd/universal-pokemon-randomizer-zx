package com.dabomstew.pkrandom.romhandlers.hack.mode;

import com.dabomstew.pkrandom.romhandlers.hack.ability.modern.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.AbilityIlluminateMode;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.AbilityRattledMode;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackModCollection;
import com.dabomstew.pkrandom.romhandlers.hack.HackMode;
import com.dabomstew.pkrandom.romhandlers.hack.pokemon.AbilityExpansionHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.weather.WeatherHailSnowMode;

public class ModernHackMode extends HackMode {
    public ModernHackMode(String name) {
        super(name);

        // Ability
        addHackMod(new AbilityExpansionHackMod());
        addHackMod(new AbilityHackModCollection(
                new AbilityHackMod_012_Oblivious_Modern(),
                new AbilityHackMod_014_CompoundEyes_Modern(),
                new AbilityHackMod_020_OwnTempo_Modern(),
                new AbilityHackMod_031_LightningRod_Modern(),
                new AbilityHackMod_035_Illuminate_Modern(),
                new AbilityHackMod_039_InnerFocus_Modern()
        ));


        // Ability
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
