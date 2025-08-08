package com.dabomstew.pkrandom.romhandlers.hack.mode;

import com.dabomstew.pkrandom.romhandlers.hack.MoveHackModCollection;
import com.dabomstew.pkrandom.romhandlers.hack.ability.custom.AbilityHackMod_051_KeenEye;
import com.dabomstew.pkrandom.romhandlers.hack.ability.expansion.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.modern.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.AbilityIlluminateMode;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.AbilityRattledMode;
import com.dabomstew.pkrandom.romhandlers.hack.AbilityHackModCollection;
import com.dabomstew.pkrandom.romhandlers.hack.HackMode;
import com.dabomstew.pkrandom.romhandlers.hack.move.expansion.*;
import com.dabomstew.pkrandom.romhandlers.hack.move.modern.*;
import com.dabomstew.pkrandom.romhandlers.hack.pokemon.AbilityExpansionHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.pokemon.MoveExpansionHackMod;
import com.dabomstew.pkrandom.romhandlers.hack.weather.WeatherHailSnowMode;

public class ModernHackMode extends HackMode {
    public ModernHackMode(String name) {
        super(name);

        // Ability
        addHackMod(new AbilityExpansionHackMod());
        addHackMod(new AbilityHackModCollection(
                new AbilityHackMod_012_Oblivious_TauntAndIntimidateImmunity(),
                new AbilityHackMod_014_CompoundEyes_NameUpdate(),
                new AbilityHackMod_020_OwnTempo_IntimidateImmunity(),
                new AbilityHackMod_031_LightningRod_NameUpdate(),
                new AbilityHackMod_035_Illuminate_Modern(),
                new AbilityHackMod_039_InnerFocus_IntimidateImmunity(),
                new AbilityHackMod_051_KeenEye(true),
                new AbilityHackMod_113_Scrappy_IntimidateImmunity(),
                // TODO: 117 Snow Warning - Hail -> Snow
                new AbilityHackMod_133_WeakArmor_Modern(),
                new AbilityHackMod_155_Rattled_IntimidateActivation(),
                new AbilityHackMod_158_Prankster_QuickGuardDarkType(),
                
                // Expansion
                new AbilityHackMod_165_AromaVeil(), // TODO
                new AbilityHackMod_166_FlowerVeil(), // TODO
                new AbilityHackMod_167_CheekPouch(), // TODO
                new AbilityHackMod_168_Protean(),
                new AbilityHackMod_169_FurCoat(),
                new AbilityHackMod_171_Bulletproof(),
                new AbilityHackMod_172_Competitive(),
                new AbilityHackMod_173_StrongJaw(),
                new AbilityHackMod_174_Refrigerate(),
                new AbilityHackMod_175_SweetVeil(), // TODO
                new AbilityHackMod_176_StanceChange(), // TODO
                new AbilityHackMod_177_GaleWings(),
                new AbilityHackMod_188_AuraBreak(), // TODO
                new AbilityHackMod_197_ShieldsDown(), // TODO
                new AbilityHackMod_199_WaterBubble(), // TODO
                new AbilityHackMod_208_Schooling(), // TODO
                new AbilityHackMod_209_Disguise(), // TODO
                new AbilityHackMod_210_BattleBond(), // TODO
                new AbilityHackMod_211_PowerConstruct(), // TODO
                new AbilityHackMod_213_Comatose(), // TODO
                new AbilityHackMod_214_QueenlyMajesty(), // TODO
                new AbilityHackMod_218_Fluffy(),
                new AbilityHackMod_219_Dazzling(), // TODO
                new AbilityHackMod_222_Receiver(), // TODO
                new AbilityHackMod_223_PowerOfAlchemy(), // TODO
                new AbilityHackMod_225_RksSystem(), // TODO
                new AbilityHackMod_240_MirrorArmor(), // TODO
                new AbilityHackMod_241_GulpMissile(), // TODO
                new AbilityHackMod_244_PunkRock(), // TODO
                new AbilityHackMod_246_IceScales(), // TODO
                new AbilityHackMod_248_IceFace(), // TODO
                new AbilityHackMod_256_NeutralizingGas(), // TODO
                new AbilityHackMod_257_PastelVeil(), // TODO
                new AbilityHackMod_266_AsOne_ChillingNeigh(), // TODO
                new AbilityHackMod_267_AsOne_GrimNeigh(), // TODO
                new AbilityHackMod_270_ThermalExchange(),
                new AbilityHackMod_272_PurifyingSalt(), // TODO
                new AbilityHackMod_273_WellBakedBody(), // TODO
                new AbilityHackMod_274_WindRider(), // TODO
                new AbilityHackMod_275_GuardDog(), // TODO
                new AbilityHackMod_278_ZeroToHero(), // TODO
                new AbilityHackMod_279_Commander(), // TODO
                new AbilityHackMod_281_Protosynthesis(), // TODO
                new AbilityHackMod_282_QuarkDrive(), // TODO
                new AbilityHackMod_283_GoodAsGold(),
                new AbilityHackMod_284_VesselOfRuin(), // TODO
                new AbilityHackMod_286_TabletsOfRuin(), // TODO
                new AbilityHackMod_288_OrichalcumPulse(), // TODO
                new AbilityHackMod_289_HadronEngine(), // TODO
                new AbilityHackMod_296_ArmorTail(), // TODO
                new AbilityHackMod_297_EarthEater(), // TODO
                new AbilityHackMod_300_MindsEye(),
                new AbilityHackMod_308_TeraShell(), // TODO
                new AbilityHackMod_310_PoisonPuppeteer() // TODO
        ));

        // Move
        addHackMod(new MoveExpansionHackMod());
        addHackMod(new MoveHackModCollection(
                new MoveHackMod_011_ViseGrip(),
                new MoveHackMod_018_Whirlwind(),
                new MoveHackMod_019_Fly(),
                new MoveHackMod_023_Stomp(),
                new MoveHackMod_046_Roar(),
                new MoveHackMod_059_Blizzard(),
                new MoveHackMod_074_Growth(),
                new MoveHackMod_076_SolarBeam(),
                new MoveHackMod_087_Thunder(),
                new MoveHackMod_091_Dig(),
                new MoveHackMod_118_Metronome(),
                new MoveHackMod_182_Protect_MoveChance(),
                new MoveHackMod_214_SleepTalk(),
                new MoveHackMod_234_MorningSun(),
                new MoveHackMod_235_Synthesis(),
                new MoveHackMod_236_Moonlight(),
                new MoveHackMod_267_NaturePower(),
                new MoveHackMod_237_HiddenPower(),
                new MoveHackMod_274_Assist(),
                new MoveHackMod_282_KnockOff(),
                new MoveHackMod_289_Snatch(),
                new MoveHackMod_291_Dive(),
                new MoveHackMod_311_WeatherBall(),
                new MoveHackMod_336_Howl(),
                new MoveHackMod_340_Bounce(),
                new MoveHackMod_382_MeFirst(),
                new MoveHackMod_383_CopyCat(),
                new MoveHackMod_449_Judgment_PixiePlate(),
                new MoveHackMod_467_ShadowForce(),
                new MoveHackMod_469_WideGuard(),
                new MoveHackMod_484_Heavy_Slam(),
                new MoveHackMod_501_QuickGuard(),
                new MoveHackMod_507_SkyDrop(),
                new MoveHackMod_542_Hurricane(),
                
                // Expansion
                new MoveHackMod_560_FlyingPress(), // TODO
                new MoveHackMod_561_MatBlock(), // TODO
                new MoveHackMod_562_Belch(),
                new MoveHackMod_564_StickyWeb(),
                new MoveHackMod_565_FellStinger(),
                new MoveHackMod_566_PhantomForce(),
                new MoveHackMod_570_ParabolicCharge(),
                new MoveHackMod_571_Spotlight(), // TODO
                new MoveHackMod_572_PetalBlizzard(),
                new MoveHackMod_573_FreezeDry(),
                new MoveHackMod_574_DisarmingVoice(),
                new MoveHackMod_577_DrainingKiss(),
                new MoveHackMod_578_CraftyShield(), // TODO
                new MoveHackMod_580_GrassyTerrain(), // TODO
                new MoveHackMod_583_PlayRough(),
                new MoveHackMod_584_FairyWind(),
                new MoveHackMod_585_Moonblast(),
                new MoveHackMod_586_Boomburst(),
                new MoveHackMod_588_KingsShield(), // TODO
                new MoveHackMod_591_DiamondStorm(),
                new MoveHackMod_592_SteamEruption(),
                new MoveHackMod_594_WaterShuriken(),
                new MoveHackMod_595_MysticalFire()
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
