package com.dabomstew.pkrandom.romhandlers.hack;

import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.pokemon.MoveCategory;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.romhandlers.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.*;
import com.dabomstew.pkrandom.romhandlers.hack.item.ItemProtectorMode;
import com.dabomstew.pkrandom.romhandlers.hack.weather.WeatherHailSnowMode;

import java.util.*;

public class HackMode {
    public final String name;

    private final Map<Class<? extends HackMod>, HackMod> hackMods = new HashMap<>();

    // Abilities    
    public int abilityStenchFlinchPercent = 10; // #001
    public AbilityDampMode abilityDampMode = AbilityDampMode.VANILLA; // #006
    public AbilityLimberMode abilityLimberMode = AbilityLimberMode.VANILLA; // #007
    public boolean abilityObliviousIgnoresTaunt = false; // #012
    public boolean abilityObliviousIgnoresIntimidate = false; // #012
    public String abilityCompoundEyesName = "Compoundeyes"; // #014
    public AbilityColorChangeMode abilityColorChangeMode = AbilityColorChangeMode.VANILLA; // #016
    public AbilityImmunityMode abilityImmunityMode = AbilityImmunityMode.VANILLA; // #017
    public String abilityShadowTagMessage; // #023
    public String abilityWonderGuardMessage; // #025
    public String abilityLightningRodName = "Lightningrod"; // #031
    public AbilityIlluminateMode abilityIlluminateMode = AbilityIlluminateMode.VANILLA; // #035
    public String abilityIlluminateMessage; // #035
    public double abilityHugePowerMultiplier = 2; // #037
    public String abilityHugePowerMessage; // #037
    public boolean abilityInnerFocusIgnoresIntimidate = false; // #039
    public AbilityMagmaArmorMode abilityMagmaArmorMode = AbilityMagmaArmorMode.VANILLA; // #040
    public AbilityWaterVeilMode abilityWaterVeilMode = AbilityWaterVeilMode.VANILLA; // #041
    public String abilityWaterVeilMessage; // #041
    public String abilityMagnetPullMessage; // #042
    public boolean abilitySoundproofIsImmuneToPerishSong = false; // #043
    public boolean abilitySandStreamAllowSelfDamage = true; // #045
    public AbilityRunAwayMode abilityRunAwayMode = AbilityRunAwayMode.VANILLA; // #050
    public AbilityKeenEyeMode abilityKeenEyeMode = AbilityKeenEyeMode.VANILLA; // #051
    public AbilityHyperCutterMode abilityHyperCutterMode = AbilityHyperCutterMode.VANILLA; // #052
    public AbilityTruantMode abilityTruantMode = AbilityTruantMode.VANILLA; // #054
    public byte abilityTruantHealFraction = 0; // #054
    public AbilityHustleMode abilityHustleMode = AbilityHustleMode.VANILLA; // #055
    public double abilityHustleAccuracyMultiplier = 0.8; // #055
    public AbilityPlusMode abilityPlusMode = AbilityPlusMode.VANILLA; // #057
    public double abilityPlusMultiplier = 1; // #057
    public String abilityPlusMessage; // #057
    public AbilityMinusMode abilityMinusMode = AbilityMinusMode.VANILLA; // #058
    public double abilityMinusMultiplier = 1; // #058
    public String abilityMinusMessage; // #058
    public String abilityArenaTrapMessage; // #071
    public AbilityVitalSpiritMode abilityVitalSpiritMode = AbilityVitalSpiritMode.VANILLA; // #072
    public MoveCategory abilityPurePowerMoveCategory = MoveCategory.PHYSICAL; // #074
    public double abilityPurePowerMultiplier = 2; // #074
    public String abilityPurePowerMessage; // #074
    public AbilityTangledFeetMode abilityTangledFeetMode = AbilityTangledFeetMode.VANILLA; // #077
    public double abilityRivalrySameGenderMultiplier = 1.25; // #079
    public double abilityRivalryOppositeGenderMultiplier = 0.75; // #079
    public boolean abilitySteadfastIgnoresTaunt = false; // #080
    public AbilityAngerPointMode abilityAngerPointMode = AbilityAngerPointMode.VANILLA; // #083
    public double abilityIronFistMultiplier = 1.2; // #089
    public String abilitySuperLuckMessage; // #105
    public AbilityIceBodyMode abilityIceBodyMode = AbilityIceBodyMode.VANILLA; // #115
    public AbilityJustifiedMode abilityJustifiedMode = AbilityJustifiedMode.VANILLA; // #154
    public AbilityRattledMode abilityRattledMode = AbilityRattledMode.VANILLA; // #155
    public boolean abilityProteanFirstOnly = true; // #168
    public double abilityFurCoatMultiplier = 2; // #169
    public String abilityFurCoatDescription = "Halves the damage\\xFFFEfrom physical moves."; // #169
    public boolean abilityMegaLauncherIncludesBallBombMoves = false; // #178
    public AbilityThermalExchangeMode abilityThermalExchangeMode = AbilityThermalExchangeMode.VANILLA; // #270
    public double abilitySharpnessMultiplier = 1.5; // #292
    public String abilityLuckyFootName = "Lucky Foot"; // #504

    // Moves
    public List<HackMod> moveCommonMods = new ArrayList<>();
    public SortedMap<Integer, MoveHackMod> moveEventMods = new TreeMap<>();
    public String tmsAndHMsFile;
    public byte trapMoveDamageFraction = 16;
    public byte trapMoveDamageFractionWithBoost = 8;
    public double screenMoveDoubleBattleReduction = 0.66;

    // Items
    public List<HackMod> itemCommonMods = new ArrayList<>();
    public SortedMap<Integer, ItemHackMod> itemEventMods = new TreeMap<>();
    public double gemItemDamageMultiplier = 1.5;
    public ItemProtectorMode itemProtectorMode = ItemProtectorMode.VANILLA; // #321

    // Status
    public byte statusBurnDamageFraction = 8;
    public boolean statusFreezeReplaceWithFrostbite = false;
    public byte statusFrostbiteDamageFraction = 0;
    public byte statusParalysisSpeedPercent = 25;
    public boolean statusSleepResetTurnsOnSwitch = true;
    public byte statusConfusionHitPercent = 50;

    // Type
    public boolean typeGhostCanAlwaysEscape = false;
    public boolean typeGrassIsImmuneToPowderMoves = false;
    public boolean typeDarkIsImmuneToPrankster = false;

    // Weather
    public double weatherDamageModStrong = 1.5;
    public double weatherDamageModWeak = 0.5;
    public WeatherHailSnowMode weatherHailSnowMode = WeatherHailSnowMode.VANILLA_HAIL;
    public Type[] weatherHailImmuneTypes = new Type[]{Type.ICE};
    public Type[] weatherSandstormImmuneTypes = new Type[]{Type.ROCK, Type.GROUND, Type.STEEL};

    // Terrain
    public boolean includePollutedTerrain = false;
    public boolean includeHauntedTerrain = false;

    // AI
    public boolean aiSimulateDamageFix = false;

    // Pokémon Data
    public abstract static class PokemonData {
    }

    public static class PokemonDataIni extends PokemonData {
        public final String filename;
        public final boolean fromModern;

        public PokemonDataIni(String filename, boolean fromModern) {
            this.filename = filename;
            this.fromModern = fromModern;
        }
    }

    public static class PokemonDataNarc extends PokemonData {
        public final String personal;
        public final String levelUpMoves;
        public final String evolutions;

        public PokemonDataNarc(String personal, String levelUpMoves, String evolutions) {
            this.personal = personal;
            this.levelUpMoves = levelUpMoves;
            this.evolutions = evolutions;
        }
    }

    public PokemonData pokemonData;
    public double[] pokemonDataExpYieldScale = new double[]{0.2, 0.35, 0.45};

    // Trainer Data
    public static class TrainerData {
        public String dataNarc;
        public String pokeNarc;

        public TrainerData(String dataNarc, String pokeNarc) {
            this.dataNarc = dataNarc;
            this.pokeNarc = pokeNarc;
        }
    }

    public TrainerData trainerData;

    // Misc.
    public short shinyRate = 8192;
    public boolean allowShinyTrainerPokemon = false;
    public double criticalHitMultiplier = 2;
    public byte[] criticalHitRatios = {16, 8, 4, 2, 1};
    public double singleTypeSTAB = 1.5;
    public double multiTypeSTAB = 1.5;
    public boolean dynamicTurnOrder = false;

    public HackMode(String name) {
        this.name = name;
    }

    protected void addHackMod(HackMod newHackMod) {
        if (hackMods.containsKey(newHackMod.getClass())) {
            HackMod hackMod = hackMods.get(newHackMod.getClass());
            hackMod.Merge(newHackMod);
            return;
        }

        hackMods.put(newHackMod.getClass(), newHackMod);
    }

    protected void removeHackMod(Class<? extends HackMod> hackModClass) {
        hackMods.remove(hackModClass);
    }

    protected <T extends HackMod> T getHackMod(Class<T> hackModClass) {
        HackMod hackMod = hackMods.get(hackModClass);
        return hackModClass.cast(hackMod);
    }
    
    public void registerGlobalValue(Gen5RomHandler romHandler, Settings settings, ArmParser armParser, ParagonLiteAddressMap globalAddressMap, ParagonLiteArm9 arm9, Map<OverlayId, ParagonLiteOverlay> overlays) {
        Collection<HackMod> hackModValues = hackMods.values();
        HackMod.Context context = new HackMod.Context(romHandler, settings, armParser, globalAddressMap, arm9, overlays, null);
        for (HackMod hackMod : hackModValues) {
            hackMod.registerGlobalValues(context);
        }
    }

    public void applyAll(Gen5RomHandler romHandler, Settings settings, ArmParser armParser, ParagonLiteAddressMap globalAddressMap, ParagonLiteArm9 arm9, Map<OverlayId, ParagonLiteOverlay> overlays) {
        Collection<HackMod> hackModValues = hackMods.values();
        Map<Class<? extends HackMod>, HackMod> applied = new HashMap<>();
        HackMod.Context context = new HackMod.Context(romHandler, settings, armParser, globalAddressMap, arm9, overlays, applied);
        for (HackMod hackMod : hackModValues) {
            if (applied.containsKey(hackMod.getClass()))
                throw new RuntimeException();

            hackMod.apply(context);
            applied.put(hackMod.getClass(), hackMod);
        }
    }
}
