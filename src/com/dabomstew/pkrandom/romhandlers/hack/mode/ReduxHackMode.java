package com.dabomstew.pkrandom.romhandlers.hack.mode;

import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.expansion.AbilityHackMod_177_GaleWings;
import com.dabomstew.pkrandom.romhandlers.hack.ability.expansion.AbilityHackMod_270_ThermalExchange;
import com.dabomstew.pkrandom.romhandlers.hack.ability.modernplus.AbilityHackMod_023_ShadowTag_Message;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.*;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackModCollection;
import com.dabomstew.pkrandom.romhandlers.hack.HackMode;
import com.dabomstew.pkrandom.romhandlers.hack.ability.original.*;
import com.dabomstew.pkrandom.romhandlers.hack.weather.WeatherHailSnowMode;

// Blaze Black 2 Redux and Volt White 2 Redux
public class ReduxHackMode extends ModernPlusHackMode {
    public ReduxHackMode() {
        super("Redux");

        // REMOVE LATER
        abilityShadowTagMessage = "{0} stalks\nthe shadows!";
        abilityHugePowerMessage = "{0} is striking\na pose!";

        // Ability
        addHackMod(new AbilityHackModCollection(
                new AbilityHackMod_001_Stench(20),
                new AbilityHackMod_006_Damp_FireResist(),
                new AbilityHackMod_007_Limber_SpeedReductionImmunity(),
                // TODO: 008 Sand Veil
                // TODO: 012 Oblivious
                new AbilityHackMod_016_ColorChange_Protean(false),
                new AbilityHackMod_017_Immunity_PoisonTypeImmunity(),
                new AbilityHackMod_023_ShadowTag_Message(abilityShadowTagMessage),
                // TODO: 028 Synchronize
                new AbilityHackMod_035_Illuminate_GhostAndDarkResistance(),
                // TODO: 039 Inner Focus
                new AbilityHackMod_040_MagmaArmor_WaterAndIceImmunity(),
                // TODO: 041 Water Veil
                // TODO: 050 Run Away
                new AbilityHackMod_051_KeenEye(true, 1.1),
                new AbilityHackMod_052_HyperCutter_IgnoreDefense(),
                // TODO: 053 Pickup
                new AbilityHackMod_054_Truant_Heal(true, true, 16, true),
                new AbilityHackMod_055_Hustle(0.9),
                new AbilityHackMod_057_Plus_SpAtkBoost(true, 1.2, abilityPlusMessage),
                new AbilityHackMod_058_Minus_SpDefBoost(true, 1.2, abilityPlusMessage),
                new AbilityHackMod_061_ShedSkin_NextTurnAlways(),
                // TODO: 064 Liquid Ooze
                // TODO: 065 Overgrow
                // TODO: 066 Blaze
                // TODO: 067 Torrent
                // TODO: 068 Swarm
                new AbilityHackMod_073_WhiteSmoke_Self(),
                new AbilityHackMod_074_PurePower(2, MoveCategory.SPECIAL, abilityHugePowerMessage),
                new AbilityHackMod_077_TangledFeet_OnMissOrFail(),
                new AbilityHackMod_079_Rivalry(1.2, 1),
                // TODO: 080 Steadfast
                // TODO: 081 Snow Cloak
                // TODO: 082 Gluttony
                new AbilityHackMod_083_AngerPoint_Miss(true),
                // TODO: 085 Heatproof
                new AbilityHackMod_089_IronFist(1.3),
                // TODO: 102 Leaf Guard
                // TODO: 107 Anticipation
                // TODO: 108 Forwarn
                new AbilityHackMod_112_SlowStart(3),
                // TODO: 119 Frisk
                new AbilityHackMod_122_FlowerGift_SpAtk(),
                new AbilityHackMod_124_Pickpocket_OnAttack(),
                new AbilityHackMod_126_Contrary_Popup(),
                new AbilityHackMod_134_HeavyMetal_SuperEffective(0.667),
                new AbilityHackMod_155_Rattled_IntimidateActivationAndBugGhostResist(),

                // Expansion
                new AbilityHackMod_177_GaleWings(AbilityHackMod_177_GaleWings.HPRequirement.Half),
                new AbilityHackMod_270_ThermalExchange(true),

                // Original
                new AbilityHackMod_500_HeavyWing(),

                new AbilityHackMod_511_SunSoaked(),

                new AbilityHackMod_538_RabbitsFoot(),
                new AbilityHackMod_542_FlutterDust(),
                new AbilityHackMod_543_FocusingLens(1.3, true)
        ));

        abilityIlluminateMode = AbilityIlluminateMode.RESIST_DARK_AND_GHOST;

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
        statusFrostbiteDamageFraction = 16;

        // Pokémon Data
        pokemonData = new HackMode.PokemonDataNarc("redux_poke_personal.narc", "redux_poke_level-up_moves.narc", "redux_poke_evolutions.narc");

        // Trainer Data
        trainerData = new HackMode.TrainerData("redux_trainer_data.narc", "redux_trainer_poke.narc");

        // Misc.
        shinyRate = 512;
    }
}
