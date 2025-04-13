package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.romhandlers.hack.Ability.*;
import com.dabomstew.pkrandom.romhandlers.hack.Weather.WeatherHailSnowMode;

// Blaze Black 2 Redux and Volt White 2 Redux
public class ReduxHackMode extends ModernPlusHackMode {
    public ReduxHackMode() {
        super("Redux");
    }

    public ReduxHackMode(String name) {
        super(name);
    }

    @Override
    protected void setValues() {
        // Ability
        abilityStenchFlinchPercent = 20; // #001
        abilityDampMode = AbilityDampMode.VANILLA_PLUS_HEATPROOF; // #006
        abilityLimberMode = AbilityLimberMode.VANILLA_PLUS_PREVENT_SPEED_DROP; // #007
        abilityColorChangeMode = AbilityColorChangeMode.ORIGINAL_PROTEAN; // #016
        abilityImmunityMode = AbilityImmunityMode.VANILLA_PLUS_IMMUNE_TO_POISON_MOVES; // #017
        abilityShadowTagMessage = "{0} stalks\nthe shadows!"; // #023
        abilityIlluminateMode = AbilityIlluminateMode.RESIST_DARK_AND_GHOST; // #035
        abilityMagmaArmorMode = AbilityMagmaArmorMode.VANILLA_PLUS_IMMUNE_TO_WATER_AND_ICE; // #040
        abilityWaterVeilMode = AbilityWaterVeilMode.VANILLA_PLUS_OVERCOAT; // #041
        abilityKeenEyeMode = AbilityKeenEyeMode.VANILLA_PLUS_IGNORES_EVASION_AND_INCREASES_ACCURACY; // #051
        abilityHyperCutterMode = AbilityHyperCutterMode.VANILLA_PLUS_USES_CRIT_STATS; // #052
        abilityTruantMode = AbilityTruantMode.VANILLA_PLUS_HEAL_EVERY_TURN_PLUS_IGNORE_FAILED_MOVES; // #054
        abilityTruantHealFraction = 16; // #054
        abilityHustleAccuracyMultiplier = 0.9; // #055
        abilityPlusMode = AbilityPlusMode.ALLY_SPECIAL_ATTACK; // #057
        abilityPlusMultiplier = 1.2; // #057
        abilityMinusMode = AbilityMinusMode.ALLY_ATTACK; // #058
        abilityArenaTrapMessage = "{0} traps\nthe arena!"; // #071
        abilityPurePowerMode = AbilityPurePowerMode.SP_ATK; // #074
        abilityPurePowerMessage = "{0} is focusing\nits mind!"; // #074
        abilityJustifiedMode = AbilityJustifiedMode.DARK_IMMUNITY; // #154
        abilityRattledMode = AbilityRattledMode.BUG_GHOST_RESIST; // #155
        abilityMegaLauncherIncludesBallBombMoves = true; // #178
        abilityThermalExchangeMode = AbilityThermalExchangeMode.RESIST; // #270
        abilitySharpnessMultiplier = 1.3; // #292
        abilityLuckyFootName = "Steel Toecap"; // #50

        // Moves
        tmsAndHMsFile = "redux_tms_hms.tsv";
        
        // Status
        statusFreezeReplaceWithFrostbite = true;
        
        // Weather
        weatherHailSnowMode = WeatherHailSnowMode.HAIL_INCREASE_SPECIAL_DEFENSE;

        // Pokémon Data
        pokemonData = new PokemonDataNarc("redux_poke_personal.narc", "redux_poke_level-up_moves.narc", "redux_poke_evolutions.narc");
        
        // Trainer Data
        trainerData = new TrainerData("redux_trainer_data.narc", "redux_trainer_poke.narc");
        
        // Misc.
        shinyRate = 512;
    }
}
