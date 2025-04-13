package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.romhandlers.hack.Ability.*;
import com.dabomstew.pkrandom.romhandlers.hack.Item.ItemProtectorMode;
import com.dabomstew.pkrandom.romhandlers.hack.Weather.WeatherHailSnowMode;

// An experimental doubles-focused Hack Mode
public class ParagonLiteHackMode extends ModernPlusHackMode {
    public ParagonLiteHackMode() {
        super("ParagonLite");
    }

    public ParagonLiteHackMode(String name) {
        super(name);
    }

    @Override
    protected void setValues() {
        // Ability
        abilityDampMode = AbilityDampMode.RAIN_EFFECT; // #006
        abilityLimberMode = AbilityLimberMode.VANILLA_PLUS_PREVENT_SPEED_DROP; // #007
        abilityImmunityMode = AbilityImmunityMode.VANILLA_PLUS_IMMUNE_TO_POISON_MOVES; // #017
        abilityIlluminateMode = AbilityIlluminateMode.BOOST_ALL_ACCURACY; // #035
        abilityHugePowerMultiplier = 1.5; // #037
        abilityMagmaArmorMode = AbilityMagmaArmorMode.RESISTS_GROUND_AND_WATER_PLUS_BURN_ON_CONTACT; // #040
        abilityKeenEyeMode = AbilityKeenEyeMode.VANILLA_PLUS_IGNORES_EVASION_AND_INCREASES_ACCURACY; // #051
        abilityHyperCutterMode = AbilityHyperCutterMode.VANILLA_PLUS_USES_CRIT_STATS; // #052
        abilityTruantMode = AbilityTruantMode.VANILLA_PLUS_HEAL_ON_LOAFING_AROUND_TURNS; // #054
        abilityTruantHealFraction = 2; // #054
        abilityHustleMode = AbilityHustleMode.INCREASE_PRIORITY_OF_LOW_POWER_MOVES; // #055
        abilityPlusMode = AbilityPlusMode.ALLY_SPECIAL_ATTACK; // #057
        abilityPlusMultiplier = 1.3; // #057
        abilityMinusMode = AbilityMinusMode.ALLY_SPECIAL_DEFENSE; // #058
        abilityVitalSpiritMode = AbilityVitalSpiritMode.INCREASE_SPECIAL_DEFENSE; // #072
        abilityPurePowerMode = AbilityPurePowerMode.SP_ATK; // #074
        abilityPurePowerMultiplier = 1.5; // #074
        abilityPurePowerMessage = "{0} is focusing\nits mind!"; // #074
        abilityIceBodyMode = AbilityIceBodyMode.ICE_IMMUNE_AND_RECOVER; // #115
        abilityJustifiedMode = AbilityJustifiedMode.DARK_IMMUNITY; // #154
        abilityFurCoatMultiplier = 1.5; // #169
        abilityFurCoatDescription = "Reduces the damage\\xFFFEfrom physical moves."; // #169
        abilityMegaLauncherIncludesBallBombMoves = true; // #178
        abilityThermalExchangeMode = AbilityThermalExchangeMode.RESIST; // #270
        abilitySharpnessMultiplier = 1.3; // #292

        // Item
        itemProtectorMode = ItemProtectorMode.DEFENSE_BOOST_NO_STATUS; // #321

        // Status
        statusFreezeReplaceWithFrostbite = true;

        // Weather
        weatherDamageModStrong = 1.3;
        weatherHailSnowMode = WeatherHailSnowMode.HAIL_INCREASE_SPECIAL_DEFENSE;
        weatherHailImmuneTypes = new Type[]{Type.FIGHTING, Type.STEEL, Type.ICE};

        // Terrain
        includePollutedTerrain = true;
        includeHauntedTerrain = true;

        // Pokémon Data
        pokemonData = new PokemonDataIni("pokes.ini", true);
        pokemonDataExpYieldScale = new double[]{0.5};
        
        // Misc.
        shinyRate = 32;
        allowShinyTrainerPokemon = true;
        criticalHitRatios = new byte[]{24, 6, 2, 1};
        multiTypeSTAB = 1.33;
    }
}
