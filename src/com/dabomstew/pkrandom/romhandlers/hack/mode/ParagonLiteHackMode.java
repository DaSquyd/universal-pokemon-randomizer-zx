package com.dabomstew.pkrandom.romhandlers.hack.mode;

import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.*;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackModCollection;
import com.dabomstew.pkrandom.romhandlers.hack.HackMode;
import com.dabomstew.pkrandom.romhandlers.hack.item.ItemProtectorMode;
import com.dabomstew.pkrandom.romhandlers.hack.weather.WeatherHailSnowMode;

import java.util.List;

// An experimental doubles-focused Hack Mode
public class ParagonLiteHackMode extends ModernPlusHackMode {
    public ParagonLiteHackMode(String name) {
        super(name);

        // Ability
        getHackMod(AbilityHackModCollection.class).addHackMods(List.of(
                new AbilityHackMod_006_Damp_RainEffect(),
                new AbilityHackMod_007_Limber_SpeedReductionImmunity(),
                new AbilityHackMod_017_Immunity_PoisonTypeImmunity(),
                new AbilityHackMod_035_Illuminate_Accuracy(1.3, "{0} illuminated\nthe area!"),
                new AbilityHackMod_037_HugePower(1.5, "{0} is flexing\nits muscles!"),
                new AbilityHackMod_040_MagmaArmor_WaterAndIceImmunity()
        ));


        // Ability
        abilityHugePowerMultiplier = 1.5; // #037
        abilityMagmaArmorMode = AbilityMagmaArmorMode.RESISTS_GROUND_AND_WATER_PLUS_BURN_ON_CONTACT; // #040
        abilityRunAwayMode = AbilityRunAwayMode.VANILLA_PLUS_CAN_ESCAPE_TRAPS; // #050
        abilityKeenEyeMode = AbilityKeenEyeMode.VANILLA_PLUS_IGNORES_EVASION_AND_INCREASES_ACCURACY; // #051
        abilityHyperCutterMode = AbilityHyperCutterMode.VANILLA_PLUS_USES_CRIT_STATS; // #052
        abilityTruantMode = AbilityTruantMode.VANILLA_PLUS_HEAL_ON_LOAFING_AROUND_TURNS; // #054
        abilityTruantHealFraction = 2; // #054
        abilityHustleMode = AbilityHustleMode.INCREASE_PRIORITY_OF_LOW_POWER_MOVES; // #055
        abilityPlusMode = AbilityPlusMode.ALLY_SPECIAL_ATTACK; // #057
        abilityPlusMultiplier = 1.3; // #057
        abilityMinusMode = AbilityMinusMode.ALLY_SPECIAL_DEFENSE; // #058
        abilityMinusMultiplier = 1.3; // #057
        abilityVitalSpiritMode = AbilityVitalSpiritMode.INCREASE_SPECIAL_DEFENSE; // #072
        abilityPurePowerMoveCategory = MoveCategory.SPECIAL; // #074
        abilityPurePowerMultiplier = 1.5; // #074
        abilityPurePowerMessage = "{0} is focusing\nits mind!"; // #074
        abilityTangledFeetMode = AbilityTangledFeetMode.BOOST_SPEED_WHEN_MOVE_FAILS; // #077
        abilityRivalryOppositeGenderMultiplier = 1.2; // #079
        abilityRivalrySameGenderMultiplier = 1; // #079
        abilityAngerPointMode = AbilityAngerPointMode.VANILLA_PLUS_BOOST_WHEN_MOVE_FAILS; // #083
        abilityIronFistMultiplier = 1.3; // #089
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
        pokemonData = new HackMode.PokemonDataIni("pokes.ini", true);
        pokemonDataExpYieldScale = new double[]{0.5};

        // Misc.
        shinyRate = 32;
        allowShinyTrainerPokemon = true;
        criticalHitRatios = new byte[]{24, 6, 2, 1};
        multiTypeSTAB = 1.33;
    }
}
