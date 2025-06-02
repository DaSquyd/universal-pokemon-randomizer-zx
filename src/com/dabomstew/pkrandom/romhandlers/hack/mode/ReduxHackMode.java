package com.dabomstew.pkrandom.romhandlers.hack.mode;

import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.*;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackModCollection;
import com.dabomstew.pkrandom.romhandlers.hack.HackMode;
import com.dabomstew.pkrandom.romhandlers.hack.weather.WeatherHailSnowMode;

import java.util.List;

// Blaze Black 2 Redux and Volt White 2 Redux
public class ReduxHackMode extends ModernPlusHackMode {
    public ReduxHackMode(String name) {
        super(name);

        // Ability
        getHackMod(AbilityHackModCollection.class).addHackMods(List.of(
                new AbilityHackMod_001_Stench(20),
                new AbilityHackMod_006_Damp_FireResist(),
                new AbilityHackMod_007_Limber_SpeedReductionImmunity(),
                new AbilityHackMod_016_ColorChange_Protean(false),
                new AbilityHackMod_017_Immunity_PoisonTypeImmunity(),
                new AbilityHackMod_023_ShadowTag_Message("{0} stalks\nthe shadows!"),
                new AbilityHackMod_035_Illuminate_GhostAndDarkResistance(),
                new AbilityHackMod_040_MagmaArmor_WaterAndIceImmunity()
        ));


        // Ability
        abilityMagmaArmorMode = AbilityMagmaArmorMode.VANILLA_PLUS_IMMUNE_TO_WATER_AND_ICE; // #040
        abilityWaterVeilMode = AbilityWaterVeilMode.VANILLA_PLUS_OVERCOAT; // #041
        abilityRunAwayMode = AbilityRunAwayMode.VANILLA_PLUS_CAN_ESCAPE_TRAPS; // #050
        abilityKeenEyeMode = AbilityKeenEyeMode.VANILLA_PLUS_IGNORES_EVASION_AND_INCREASES_ACCURACY; // #051
        abilityHyperCutterMode = AbilityHyperCutterMode.VANILLA_PLUS_USES_CRIT_STATS; // #052
        abilityTruantMode = AbilityTruantMode.VANILLA_PLUS_HEAL_EVERY_TURN_PLUS_IGNORE_FAILED_MOVES; // #054
        abilityTruantHealFraction = 16; // #054
        abilityHustleAccuracyMultiplier = 0.9; // #055
        abilityPlusMode = AbilityPlusMode.ALLY_SPECIAL_ATTACK; // #057
        abilityPlusMultiplier = 1.2; // #057
        abilityMinusMode = AbilityMinusMode.ALLY_SPECIAL_DEFENSE; // #058
        abilityMinusMultiplier = 1.2; // #057
        abilityArenaTrapMessage = "{0} traps\nthe arena!"; // #071
        abilityPurePowerMoveCategory = MoveCategory.SPECIAL; // #074
        abilityPurePowerMessage = "{0} is focusing\nits mind!"; // #074
        abilityTangledFeetMode = AbilityTangledFeetMode.BOOST_SPEED_WHEN_MOVE_FAILS; // #077
        abilityRivalryOppositeGenderMultiplier = 1.2; // #079
        abilityRivalrySameGenderMultiplier = 1; // #079
        abilitySteadfastIgnoresTaunt = true; // #080
        abilityAngerPointMode = AbilityAngerPointMode.VANILLA_PLUS_BOOST_WHEN_MOVE_FAILS; // #083
        abilityIronFistMultiplier = 1.3; // #089
        abilityJustifiedMode = AbilityJustifiedMode.DARK_IMMUNITY; // #154
        abilityRattledMode = AbilityRattledMode.BUG_GHOST_RESIST; // #155
        abilityMegaLauncherIncludesBallBombMoves = true; // #178
        abilityThermalExchangeMode = AbilityThermalExchangeMode.RESIST; // #270
        abilitySharpnessMultiplier = 1.3; // #292
        abilityLuckyFootName = "Steel Toecap"; // #504

        // Moves
        tmsAndHMsFile = "redux_tms_hms.tsv";

        // Status
        statusFreezeReplaceWithFrostbite = true;

        // Weather
        weatherHailSnowMode = WeatherHailSnowMode.HAIL_INCREASE_SPECIAL_DEFENSE;

        // Pokémon Data
        pokemonData = new HackMode.PokemonDataNarc("redux_poke_personal.narc", "redux_poke_level-up_moves.narc", "redux_poke_evolutions.narc");

        // Trainer Data
        trainerData = new HackMode.TrainerData("redux_trainer_data.narc", "redux_trainer_poke.narc");

        // Misc.
        shinyRate = 512;
    }
}
