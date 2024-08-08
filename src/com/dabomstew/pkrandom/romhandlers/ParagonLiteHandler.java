package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.BitmapFile;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.GFXFunctions;
import com.dabomstew.pkrandom.Utils;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.*;
import com.sun.source.doctree.UnknownBlockTagTree;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.DataFormatException;

public class ParagonLiteHandler {

    Gen5RomHandler romHandler;

    ParagonLiteAddressMap globalAddressMap;

    ParagonLiteOverlay arm9;
    ParagonLiteOverlay battleOvl;
    ParagonLiteOverlay battleServerOvl;
    ParagonLiteOverlay trainerAIOvl;

    Pokemon[] pokes;
    Move[] moves;

    NARCArchive pokemonGraphicsNarc;
    NARCArchive itemDataNarc;
    NARCArchive itemGraphicsNarc;

    List<String> battleEventStrings1;
    List<String> battleEventStrings2;

    List<String> abilityNames;
    List<String> abilityDescriptions;
    List<String> abilityExplanations;

    List<String> moveNames;
    List<String> moveDescriptions;

    List<String> itemNames;
    List<String> itemNameMessages;
    List<String> itemPluralNames;
    List<String> itemDescriptions;

    // Update
    List<String> oldAbilityNames;
    List<String> oldMoveNames;
    Map<Integer, String> abilityUpdates;
    PokeUpdate[] pokeUpdates;

    boolean debugMode;

    private static class PokeUpdate {
        int type1;
        int type2;

        int ability1;
        int ability2;
        int ability3;

        int hp;
        int attack;
        int defense;
        int spatk;
        int spdef;
        int speed;

        int expYield;

        int hpEVs;
        int attackEVs;
        int defenseEVs;
        int spatkEVs;
        int spdefEVs;
        int speedEVs;

        boolean hasAnyUpdate(List<String> oldAbilityNames, List<String> newAbilityNames) {
            return hasTypeUpdate() || hasAbilityUpdate(oldAbilityNames, newAbilityNames) || hasStatsUpdate() || hasExpYieldUpdate();
        }

        boolean hasTypeUpdate() {
            return type1 != 0 || type2 != 0;
        }

        boolean hasAbilityUpdate(List<String> oldAbilityNames, List<String> newAbilityNames) {
            return ability1 != 0 || ability2 != 0 || ability3 != 0
                    // Ability name changed
                    || !oldAbilityNames.get(ability1).equals(newAbilityNames.get(ability1))
                    || !oldAbilityNames.get(ability2).equals(newAbilityNames.get(ability2))
                    || !oldAbilityNames.get(ability3).equals(newAbilityNames.get(ability3));
        }

        boolean hasStatsUpdate() {
            return hp != 0 || attack != 0 || defense != 0 || spatk != 0 || spdef != 0 || speed != 0;
        }

        int bst() {
            return hp + attack + defense + spatk + spdef + speed;
        }

        boolean hasExpYieldUpdate() {
            return expYield != 0;
        }

        boolean hasEVYieldUpdate() {
            return hpEVs != 0 || attackEVs != 0 || defenseEVs != 0 || spatkEVs != 0 || spdefEVs != 0 || speedEVs != 0;
        }
    }

    ParagonLiteHandler(Gen5RomHandler romHandler, byte[] arm9Data, int battleOvlNumber, int battleServerOvlNumber, int trainerAIOvlNumber,
                       Pokemon[] pokes, Move[] moves, NARCArchive pokemonGraphicsNarc, NARCArchive itemDataNarc,
                       NARCArchive itemGraphicsNarc, List<String> battleEventStrings1, List<String> battleEventStrings2,
                       List<String> abilityNames, List<String> abilityDescriptions, List<String> abilityExplanations,
                       List<String> moveNames, List<String> moveDescriptions, List<String> itemNames, List<String> itemNameMessages,
                       List<String> itemPluralNames, List<String> itemDescriptions) {
        this.romHandler = romHandler;

        globalAddressMap = new ParagonLiteAddressMap();
        try {
            arm9 = new ParagonLiteArm9(romHandler, arm9Data, globalAddressMap);

            byte[] battleOvlData = romHandler.readOverlay(battleOvlNumber);
            int battleOvlAddress = romHandler.getOverlayAddress(battleOvlNumber);
            battleOvl = new ParagonLiteOverlay(romHandler, battleOvlNumber, "Battle", battleOvlData, battleOvlAddress,
                    ParagonLiteOverlay.Insertion.Front, globalAddressMap);

            byte[] battleServerOvlData = romHandler.readOverlay(battleServerOvlNumber);
            int battleServerOvlAddress = romHandler.getOverlayAddress(battleServerOvlNumber);
            battleServerOvl = new ParagonLiteOverlay(romHandler, battleServerOvlNumber, "BattleServer", battleServerOvlData,
                    battleServerOvlAddress, ParagonLiteOverlay.Insertion.Front, globalAddressMap);

            byte[] trainerAIOvlData = romHandler.readOverlay(trainerAIOvlNumber);
            int trainerAIOvlAddress = romHandler.getOverlayAddress(trainerAIOvlNumber);
            trainerAIOvl = new ParagonLiteOverlay(romHandler, trainerAIOvlNumber, "TrainerAI", trainerAIOvlData, trainerAIOvlAddress,
                    ParagonLiteOverlay.Insertion.Back, globalAddressMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int gameIndex = romHandler.getGen5GameIndex();
        if (gameIndex < 0 || gameIndex > 3)
            throw new RuntimeException();

        int namespaceColumn = 0;
        int funcNameColumn = 1;
        int typeColumn = 2;
        int dataLenColumn = 3;
        int encodingColumn = 4;
        int addressStartColumn = 5;

        List<String> lines = readLines("offsets.tsv");
        int total = lines.size() - 1;

        System.out.println("parsing offsets...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < total; ++i) {
            String line = lines.get(i + 1);
            String[] values = line.split("\t");

            Utils.printProgress(total, i, String.format("%s::%s", values[namespaceColumn], values[funcNameColumn]));

            String namespace = values[namespaceColumn];
            String label = values[funcNameColumn];
            if (addressStartColumn + gameIndex >= values.length)
                throw new RuntimeException();

            String offsetStr = values[addressStartColumn + gameIndex];
            if (offsetStr.equals("--"))
                continue;

            if (!offsetStr.startsWith("0x"))
                throw new RuntimeException();

            offsetStr = offsetStr.substring(2); // remove 0x
            int romAddress = Integer.parseUnsignedInt(offsetStr, 16);
            // TODO: Get RAM Address instead

            int dataLen;
            switch (values[typeColumn].toLowerCase()) {
                case "code":
                    int encoding = Integer.parseInt(values[encodingColumn]);
                    globalAddressMap.registerCodeAddress(namespace, label, romAddress, encoding, false);
                    break;
                case "data":
                    dataLen = Integer.parseInt(values[dataLenColumn]);
                    String refPattern = values[encodingColumn];
                    globalAddressMap.registerDataAddress(namespace, label, romAddress, dataLen, refPattern, false);
                    break;
                case "inst":
                    dataLen = Integer.parseInt(values[dataLenColumn]);
                    globalAddressMap.registerDataAddress(namespace, label, romAddress, dataLen, "", false);
                    break;
                default:
                    throw new RuntimeException(String.format("Unknown type \"%s\"", values[typeColumn]));
            }
        }
        Utils.printProgressFinished(startTime, total);

        globalAddressMap.addAllReferences();

        this.pokes = pokes;
        this.moves = moves;

        this.pokemonGraphicsNarc = pokemonGraphicsNarc;
        this.itemDataNarc = itemDataNarc;
        this.itemGraphicsNarc = itemGraphicsNarc;

        this.battleEventStrings1 = battleEventStrings1;
        this.battleEventStrings2 = battleEventStrings2;

        this.abilityNames = abilityNames;
        this.abilityDescriptions = abilityDescriptions;
        this.abilityExplanations = abilityExplanations;

        this.moveNames = moveNames;
        this.moveDescriptions = moveDescriptions;

        this.itemNames = itemNames;
        this.itemNameMessages = itemNameMessages;
        this.itemPluralNames = itemPluralNames;
        this.itemDescriptions = itemDescriptions;

        abilityUpdates = new HashMap<>();

        oldAbilityNames = new ArrayList<>();
        oldAbilityNames.addAll(abilityNames);

        oldMoveNames = new ArrayList<>();
        oldMoveNames.addAll(moveNames);

        pokeUpdates = new PokeUpdate[pokes.length];
        for (int i = 0; i < pokeUpdates.length; ++i) {
            pokeUpdates[i] = new PokeUpdate();
        }
    }

    public void writeOverlays() {
        System.out.print("Writing overlays");
        long startTime = System.currentTimeMillis();
        arm9.save(romHandler);
        battleOvl.save(romHandler);
        battleServerOvl.save(romHandler);
        trainerAIOvl.save(romHandler);
        System.out.printf(" - done, time=%dms\n", System.currentTimeMillis() - startTime);
    }

    public void setBattleEventStrings() {
        setBattleEventStrings1();
        setBattleEventStrings2();
    }

    private void setBattleEventStrings1() {
        while (battleEventStrings1.size() < 221)
            battleEventStrings1.add("");

        // Assault Vest
        battleEventStrings1.add /* 221 */("The effects of the Assault Vest prevent\\xFFFEstatus moves from being used!");
        battleEventStrings1.add /* 222 */("\uF000Č\\x0001\\x0000 can't use status moves!");
    }

    private void setBattleEventStrings2() {
        // Limber
        battleEventStrings2.add /* 1159 */("\uF000Ă\\x0001\\x0000's Speed\\xFFFEwas not lowered!");
        battleEventStrings2.add /* 1160 */("The wild \uF000Ă\\x0001\\x0000's Speed\\xFFFEwas not lowered!");
        battleEventStrings2.add /* 1161 */("The foe's \uF000Ă\\x0001\\x0000's Speed\\xFFFEwas not lowered!");

        // Plus
        battleEventStrings2.add/* 1162 */("\uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a positive charge!");
        battleEventStrings2.add/* 1163 */("The wild \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a positive charge!");
        battleEventStrings2.add/* 1164 */("The foe's \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a positive charge!");

        // Minus
        battleEventStrings2.add/* 1165 */("\uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a negative charge!");
        battleEventStrings2.add/* 1166 */("The wild \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a negative charge!");
        battleEventStrings2.add/* 1167 */("The foe's \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a negative charge!");

        // Super Luck
        battleEventStrings2.add/* 1168 */("\uF000Ă\\x0001\\x0000 is feeling lucky!");
        battleEventStrings2.add/* 1169 */("The wild \uF000Ă\\x0001\\x0000 is feeling lucky!");
        battleEventStrings2.add/* 1170 */("The foe's \uF000Ă\\x0001\\x0000 is feeling lucky!");

        // Huge Power
        battleEventStrings2.add/* 1171 */("\uF000Ă\\x0001\\x0000 is flexing\\xFFFEits muscles!");
        battleEventStrings2.add/* 1172 */("The wild \uF000Ă\\x0001\\x0000 is flexing\\xFFFEits muscles!");
        battleEventStrings2.add/* 1173 */("The foe's \uF000Ă\\x0001\\x0000 is flexing\\xFFFEits muscles!");

        // Pure Power
        battleEventStrings2.add/* 1174 */("\uF000Ă\\x0001\\x0000 is focusing\\xFFFEits mind!");
        battleEventStrings2.add/* 1175 */("The wild \uF000Ă\\x0001\\x0000 is focusing\\xFFFEits mind!");
        battleEventStrings2.add/* 1176 */("The foe's \uF000Ă\\x0001\\x0000 is focusing\\xFFFEits mind!");

        // Magnet Pull
        battleEventStrings2.add/* 1177 */("\uF000Ă\\x0001\\x0000 is generating\\xFFFEa magnetic field!");
        battleEventStrings2.add/* 1178 */("The wild \uF000Ă\\x0001\\x0000 is generating\\xFFFEa magnetic field!");
        battleEventStrings2.add/* 1179 */("The foe's \uF000Ă\\x0001\\x0000 is generating\\xFFFEa magnetic field!");

        // Shadow Tag
        battleEventStrings2.add/* 1180 */("\uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");
        battleEventStrings2.add/* 1181 */("The wild \uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");
        battleEventStrings2.add/* 1182 */("The foe's \uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");

        // Arena Trap
        battleEventStrings2.add/* 1183 */("\uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");
        battleEventStrings2.add/* 1184 */("The wild \uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");
        battleEventStrings2.add/* 1185 */("The foe's \uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");

        // Wonder Guard
        battleEventStrings2.add/* 1186 */("\uF000Ă\\x0001\\x0000 is cloaked in\\xFFFEa mysterious power!");
        battleEventStrings2.add/* 1187 */("The wild \uF000Ă\\x0001\\x0000 is cloaked in\\xFFFEa mysterious power!");
        battleEventStrings2.add/* 1188 */("The foe's \uF000Ă\\x0001\\x0000 is cloaked in\\xFFFEa mysterious power!");

        // Assault Vest
        battleEventStrings2.add/* 1189 */("The effects of the Assault Vest prevent\\xFFFEstatus moves from being used!");
        battleEventStrings2.add/* 1190 */("\uF000Č\\x0001\\x0000 can't use status moves!");

        // Weather Rocks
        battleEventStrings2.add/* 1191 */("\uF000Ă\\x0001\\x0000's \uF000Ć\\x0001\\x0001\\xFFFEbegan to glow!");
        battleEventStrings2.add/* 1192 */("The wild \uF000Ă\\x0001\\x0000's \uF000Ć\\x0001\\x0001\\xFFFEbegan to glow!");
        battleEventStrings2.add/* 1193 */("The foe's \uF000Ă\\x0001\\x0000's \uF000Ć\\x0001\\x0001\\xFFFEbegan to glow!");

        // Stone Home
        battleEventStrings2.add/* 1194 */("\uF000Ă\\x0001\\x0000 provided its allies with\\xFFFEits stone home!");
        battleEventStrings2.add/* 1195 */("The wild \uF000Ă\\x0001\\x0000 provided its allies with\\xFFFEits stone home!");
        battleEventStrings2.add/* 1196 */("The foe's \uF000Ă\\x0001\\x0000 provided its allies with\\xFFFEits stone home!");
    }

    public void setDamageCalcDefensiveStat() {
        // Grants Ice-type Pokémon a 1.5x Defense boost in hail
        // Makes Fighting-type Pokémon immune to hail damage
        List<String> lines = readLines("damage_calc_get_defensive_stat.s");
        battleOvl.replaceCode(lines, "DamageCalc_GetDefensiveStat");
        System.out.println("Set damage calc defensive stat");
    }

    public void setCritRatio() {
        byte[] critChanceData = new byte[]{24, 8, 2, 1, 1};
        battleOvl.writeData(critChanceData, "Data_CriticalHitChances");

        System.out.println("Set critical hit ratios");
    }

    public void setCritDamage() {
        // Modernizes critical hit damage
        // 2.0x -> 1.5x

        int critLogicAddress = battleOvl.find(Gen5Constants.critLogicLocator);

        ArmParser armParser = new ArmParser(globalAddressMap);

        List<String> lines = readLines("crit.s");
        byte[] critLogicData = armParser.parse(lines);

        battleOvl.writeBytes(critLogicAddress, critLogicData);
        System.out.println("Set critical hit damage");
    }

    public void setBurnDamage() {
        // Modernizes Burn damage
        // 1/8 -> 1/16
        List<String> lines = readLines("get_status_damage.s");
        battleOvl.writeCodeForceInline(lines, "GetStatusDamage");
        System.out.println("Set Burn damage");
    }

    public void setTrapDamage() {
        // Modernizes the damage of the Trapped condition
        // 1/16 -> 1/8 without Binding Band
        // 1/8  -> 1/6 with Binding Band

        int runTrappedFuncAddress = globalAddressMap.getRamAddress(battleServerOvl, "RunTrapped");

        // TODO: Trap damage => 1/8 (1/6 with Binding Band)
        battleServerOvl.writeByte(runTrappedFuncAddress + 0x8E, 6);
        battleServerOvl.writeByte(runTrappedFuncAddress + 0x94, 8);
        System.out.println("Set Trapped damage");
    }

    public void setTypeForPlate() {
        // Adds Pixie Plate functionality for Judgment and Arceus
        List<String> lines = readLines("get_type_for_plate.s");
        arm9.writeCodeForceInline(lines, "GetTypeForPlate");

        int[] arceusFairyStandard = new int[]{
                // Background (Transparent)
                GFXFunctions.makeARGBColor(104, 200, 152),
                
                // Body Main
                GFXFunctions.makeARGBColor(224, 224, 232),
                GFXFunctions.makeARGBColor(176, 176, 184),
                GFXFunctions.makeARGBColor(128, 128, 136),
                GFXFunctions.makeARGBColor(88, 88, 88),
                
                // Body Highlight
                GFXFunctions.makeARGBColor(176, 112, 136),
                GFXFunctions.makeARGBColor(152, 80, 112),
                GFXFunctions.makeARGBColor(96, 48, 72),
                
                // Ring
                GFXFunctions.makeARGBColor(248, 208, 216),
                GFXFunctions.makeARGBColor(240, 184, 196),
                GFXFunctions.makeARGBColor(184, 128, 152),
                GFXFunctions.makeARGBColor(120, 88, 104),
                
                // Gems/Eye
                GFXFunctions.makeARGBColor(240, 128, 184),
                GFXFunctions.makeARGBColor(160, 56, 104),
                
                // Iris
                GFXFunctions.makeARGBColor(152, 160, 168),
        };
        int[] arceusFairyShiny = Arrays.copyOf(arceusFairyStandard, arceusFairyStandard.length);
        arceusFairyShiny[1] = GFXFunctions.makeARGBColor(248, 240, 136);
        arceusFairyShiny[2] = GFXFunctions.makeARGBColor(216, 192, 56);
        arceusFairyShiny[3] = GFXFunctions.makeARGBColor(168, 128, 64);
        arceusFairyShiny[4] = GFXFunctions.makeARGBColor(104, 80, 32);

        byte[] arceusFairyStandardPalette = BitmapFile.writePaletteFileFromColors(arceusFairyStandard);
        byte[] arceusFairyShinyPalette = BitmapFile.writePaletteFileFromColors(arceusFairyShiny);
        pokemonGraphicsNarc.files.add(arceusFairyStandardPalette);
        pokemonGraphicsNarc.files.add(arceusFairyShinyPalette);
        
        pokes[Species.arceus].specialForms = 18;
        
        System.out.println("Set Types for Plates");
    }

    public void setGemDamageBoost() {
        // Modernizes Gem damage
        // 1.5x -> 1.3x
        List<String> lines = readLines("gem_damage_boost.s");
        battleOvl.replaceCode(lines, "CommonGemDamageBoost");
        System.out.println("Set Gem damage boost");
    }

    public void setMultiStrikeLoadedDice() {
        // Implements logic for Loaded Dice
        // Multi-strike moves with multiple accuracy checks (like Triple Kick) only evaluate once
        // Moves that hit 2-5 times will always do 4 or 5 hits
        List<String> lines = readLines("set_multi_strike_data.s");
        battleOvl.replaceCode(lines, "SetMultiStrikeData");

        System.out.println("Set multi-strike");
    }

    public void setMoveRestrictions() {
        // Implements logic for Assault Vest
        List<String> lines = readLines("move_restriction_new.s");
        battleOvl.replaceCode(lines, "TryMoveRestriction");

        System.out.println("Set move restrictions");
    }

    public void setWeatherDamage() {
        // Grants Fighting-type Pokémon immunity to hail
        List<String> lines = readLines("is_poke_damaged_by_weather.s");
        battleOvl.replaceCode(lines, "IsPokeDamagedByWeather");

        System.out.println("Set weather damage");
    }

    public void setShinyRate() {
        // Increases shiny odds
        List<String> lines = readLines("shiny_32.s");
        arm9.writeCodeForceInline(lines, "IsShiny");

        System.out.println("Set shiny rate");
    }

    public void setPokemonData() {
        Scanner sc;
        try {
            sc = new Scanner(FileFunctions.openConfig("paragonlite/pokes.ini"), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Pokemon poke = null;
        PokeUpdate pokeUpdate = null;
        while (sc.hasNextLine()) {
            String q = sc.nextLine().trim();
            if (q.contains("#")) {
                q = q.substring(0, q.indexOf("#")).trim();
            }

            if (q.isEmpty())
                continue;

            if (q.startsWith("[") && q.endsWith("]")) {
                // New poke
                String numStr = q.substring(1, 4);
                int num = Integer.parseInt(numStr);
                poke = pokes[num];
                pokeUpdate = pokeUpdates[num];
                continue;
            }

            if (poke == null) {
                System.err.println("no poke at " + q);
                continue;
            }

            String[] r = q.split("=", 2);
            if (r.length == 1) {
                System.err.println("invalid entry " + q);
                continue;
            }

            if (r[1].endsWith("\r\n")) {
                r[1] = r[1].substring(0, r[1].length() - 2);
            }

            String key = r[0].trim();
            String value = r[1].trim();

            switch (key) {
                case "Type": {
                    int newPrimaryType;
                    int newSecondaryType;

                    if (value.startsWith("[") && value.endsWith("]")) {
                        String[] typeStrs = value.substring(1, value.length() - 1).split(",", 2);
                        newPrimaryType = Type.valueOf(typeStrs[0].trim().toUpperCase()).ordinal();
                        newSecondaryType = typeStrs.length == 2 ? Type.valueOf(typeStrs[1].trim().toUpperCase()).ordinal() : -1;
                    } else {
                        newPrimaryType = Type.valueOf(value.toUpperCase()).ordinal();
                        newSecondaryType = -1;
                    }

                    pokeUpdate.type1 += newPrimaryType - poke.primaryType.ordinal();
                    pokeUpdate.type2 += newSecondaryType - (poke.secondaryType == null ? -1 : poke.secondaryType.ordinal());

                    poke.primaryType = Type.values()[newPrimaryType];
                    poke.secondaryType = newSecondaryType < 0 ? null : Type.values()[newSecondaryType];

                    break;
                }
                case "Stats": {
                    String[] statStrs = value.substring(1, value.length() - 1).split(",", 6);
                    if (statStrs.length < 6) {
                        System.err.println("invalid entry " + q);
                        continue;
                    }

                    int newHp = Integer.parseUnsignedInt(statStrs[0].trim());
                    int newAttack = Integer.parseUnsignedInt(statStrs[1].trim());
                    int newDefense = Integer.parseUnsignedInt(statStrs[2].trim());
                    int newSpatk = Integer.parseUnsignedInt(statStrs[3].trim());
                    int newSpdef = Integer.parseUnsignedInt(statStrs[4].trim());
                    int newSpeed = Integer.parseUnsignedInt(statStrs[5].trim());

                    pokeUpdate.hp += newHp - poke.hp;
                    pokeUpdate.attack += newAttack - poke.attack;
                    pokeUpdate.defense += newDefense - poke.defense;
                    pokeUpdate.spatk += newSpatk - poke.spatk;
                    pokeUpdate.spdef += newSpdef - poke.spdef;
                    pokeUpdate.speed += newSpeed - poke.speed;

                    poke.hp = newHp;
                    poke.attack = newAttack;
                    poke.defense = newDefense;
                    poke.spatk = newSpatk;
                    poke.spdef = newSpdef;
                    poke.speed = newSpeed;

                    // Standard Exp Yield is 0.45 for stage 3, 0.35 for stage 2, and 0.2 for stage 1
                    // Our standard is a flat 0.45
                    int newExpYield = (int) Math.round(poke.bst() * 0.45);
                    pokeUpdate.expYield += newExpYield - poke.expYield;
                    poke.expYield = newExpYield;

                    setEVYield(poke, pokeUpdate);

                    break;
                }
                case "Ability": {
                    // One ability
                    int newAbility = abilityNames.indexOf(value);

                    if (newAbility < 0) {
                        System.err.println("invalid ability \"" + value + "\" on " + poke.name);
                        continue;
                    }

                    pokeUpdate.ability1 += newAbility - poke.ability1;
                    pokeUpdate.ability2 += newAbility - poke.ability2;
                    pokeUpdate.ability3 += newAbility - poke.ability3;

                    poke.ability1 = newAbility;
                    poke.ability2 = newAbility;
                    poke.ability3 = newAbility;

                    break;
                }
                case "Abilities": {
                    // Three abilities
                    String[] abilityStrs = value.substring(1, value.length() - 1).split(",", 3);
                    if (abilityStrs.length < 3) {
                        System.err.println("invalid entry " + q);
                        continue;
                    }

                    for (int i = 0; i < 3; ++i)
                        abilityStrs[i] = abilityStrs[i].trim();

                    int newAbility1 = Math.max(0, abilityNames.indexOf(abilityStrs[0]));
                    if (newAbility1 == 0) {
                        System.err.println("invalid ability \"" + abilityStrs[0] + "\" on " + poke.name);
                        continue;
                    }

                    int newAbility2 = Math.max(0, abilityNames.indexOf(abilityStrs[1]));
                    if (newAbility2 == 0) {
                        System.err.println("invalid ability \"" + abilityStrs[1] + "\" on " + poke.name);
                        continue;
                    }

                    int newAbility3 = Math.max(0, abilityNames.indexOf(abilityStrs[2]));
                    if (newAbility3 == 0) {
                        System.err.println("invalid ability \"" + abilityStrs[2] + "\" on " + poke.name);
                        continue;
                    }

                    pokeUpdate.ability1 += newAbility1 - poke.ability1;
                    pokeUpdate.ability2 += newAbility2 - poke.ability2;
                    pokeUpdate.ability3 += newAbility3 - poke.ability3;

                    poke.ability1 = newAbility1;
                    poke.ability2 = newAbility2;
                    poke.ability3 = newAbility3;

                    break;
                }
                case "EVYield": {
                    String[] evYieldStrs = value.split(",", 6);
                    if (evYieldStrs.length < 6) {
                        System.err.println("invalid entry " + q);
                        continue;
                    }

                    int newHpEVs = Integer.parseUnsignedInt(evYieldStrs[0].trim());
                    int newAttackEVs = Integer.parseUnsignedInt(evYieldStrs[1].trim());
                    int newDefenseEVs = Integer.parseUnsignedInt(evYieldStrs[2].trim());
                    int newSpatkEVs = Integer.parseUnsignedInt(evYieldStrs[3].trim());
                    int newSpdefEVs = Integer.parseUnsignedInt(evYieldStrs[4].trim());
                    int newSpeedEVs = Integer.parseUnsignedInt(evYieldStrs[5].trim());

                    pokeUpdate.hpEVs += newHpEVs - poke.hpEVs;
                    pokeUpdate.attackEVs += newAttackEVs - poke.attackEVs;
                    pokeUpdate.defenseEVs += newDefenseEVs - poke.defenseEVs;
                    pokeUpdate.spatkEVs += newSpatkEVs - poke.spatkEVs;
                    pokeUpdate.spdefEVs += newSpdefEVs - poke.spdefEVs;
                    pokeUpdate.speedEVs += newSpeedEVs - poke.speedEVs;

                    poke.hpEVs = newHpEVs;
                    poke.attackEVs = newAttackEVs;
                    poke.defenseEVs = newDefenseEVs;
                    poke.spatkEVs = newSpatkEVs;
                    poke.spdefEVs = newSpdefEVs;
                    poke.speedEVs = newSpeedEVs;

                    break;
                }
                case "ExpYield": {
                    int newExpYield = Integer.parseUnsignedInt(value);
                    pokeUpdate.expYield += newExpYield - poke.expYield;
                    poke.expYield = newExpYield;
                }
            }
        }

        System.out.println("Set Pokémon data");
    }

    private static void setEVYield(Pokemon poke, PokeUpdate pokeUpdate) {
        int stage = poke.evolutionsFrom.isEmpty() ? 3 : poke.stage;

        int[] evs = new int[6];

        List<Integer> bestStats = new ArrayList<>();
        int bestStatValue = 0;

        int[] applyOrder = new int[]{0, 1, 3, 5, 2, 4};
        for (int i : applyOrder) {
            if (poke.getStatByIndex(i) < bestStatValue)
                continue;

            if (poke.getStatByIndex(i) > bestStatValue)
                bestStats.clear();

            bestStats.add(i);
            bestStatValue = poke.getStatByIndex(i);
        }

        for (int i = 0; i < stage; ++i) {
            int bestStatIndex = bestStats.get(i % bestStats.size());
            ++evs[bestStatIndex];
        }

        pokeUpdate.hpEVs += evs[0] - poke.hpEVs;
        pokeUpdate.attackEVs += evs[1] - poke.attackEVs;
        pokeUpdate.defenseEVs += evs[2] - poke.defenseEVs;
        pokeUpdate.spatkEVs += evs[3] - poke.spatkEVs;
        pokeUpdate.spdefEVs += evs[4] - poke.spdefEVs;
        pokeUpdate.speedEVs += evs[5] - poke.speedEVs;

        poke.hpEVs = evs[0];
        poke.attackEVs = evs[1];
        poke.defenseEVs = evs[2];
        poke.spatkEVs = evs[3];
        poke.spdefEVs = evs[4];
        poke.speedEVs = evs[5];
    }

    public void setTypeEffectiveness() {

    }

    public void setTrainerAIScripts(String trainerAIScriptNarcPath) {
//        // TODO: This is used for the multiplicative version of move selection
//        trainerAIOvl.writeByte(0x0217F842, 4096);

        try {
            // Trainer AI Scripts NARC
            NARCArchive narc = romHandler.readNARC(trainerAIScriptNarcPath);

//            writeTrainerAIFile(narc, 0); // Basic
//            writeTrainerAIFile(narc, 2); // Smart
//            writeTrainerAIFile(narc, 7); // Double Battle

            writeTrainerAIFile(narc, 14); // Test

            romHandler.writeNARC(trainerAIScriptNarcPath, narc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        // New OP: Add Stored to Score (0x2B)
//        List<String> addStoredToScorelines = readLines("traineraiscripts/add_stored_to_score.s");
//        int addStoredToScoreAddress = trainerAIOvl.writeArm(addStoredToScorelines, armParser);
//        trainerAIOvl.writeWord(0x02181DA0, addStoredToScoreAddress + 1);

//        // New OP: Get Damage Prediction (0x2B)
//        List<String> getDamageLines = readLines("traineraiscripts/get_damage.s");
//        int getDamageAddress = trainerAIOvl.writeArm(getDamageLines, armParser);
//        trainerAIOvl.writeWord(0x02181DE4, getDamageAddress + 1);

        System.out.println("Set trainer AI scripts");
    }

    public void setTrainers() {
        arm9.writeCode(readLines("lcg.s"), "LCG");
        arm9.writeCode(readLines("lcg_seed.s"), "SeedLCG");

        arm9.writeCodeForceInline(readLines("get_trainer_data.s"), "GetTrainerData");

        System.out.println("Set trainers");
    }

    public void setAbilities() {
        registerAbilityEffects();

        int newAbilityCount = 20;

        // Move AbilityList
        relocateAbilityListRamAddress(newAbilityCount);

        List<String> newStrs = Arrays.asList(new String[newAbilityCount]);
        abilityNames.addAll(newStrs);
        abilityDescriptions.addAll(newStrs);
        if (romHandler.isWhite2() || romHandler.isBlack2()) {
            String eggExplanation = abilityExplanations.remove(abilityExplanations.size() - 1);
            abilityExplanations.addAll(newStrs);
            abilityExplanations.add(eggExplanation);
        }

        int totalChanges = 53;
        int currentChanges = 0;
        long startTime = System.currentTimeMillis();
        System.out.println("setting abilities...");

        // #007 Limber (+ no speed drop)
        Utils.printProgress(totalChanges, currentChanges++, "Limber");
        setLimber();

        // #012 Oblivious
        Utils.printProgress(totalChanges, currentChanges++, "Oblivious");
        setOblivious();

        // #014 Compoundeyes -> Compound Eyes
        Utils.printProgress(totalChanges, currentChanges++, "Compound Eyes");
        setCompoundEyes();

        // #017 Immunity (+ Poison-type immunity)
        Utils.printProgress(totalChanges, currentChanges++, "Immunity");
        setImmunity();

        // #023 Shadow Tag
        Utils.printProgress(totalChanges, currentChanges++, "Shadow Tag");
        setShadowTag();

        // #025 Wonder Guard
        Utils.printProgress(totalChanges, currentChanges++, "Wonder Guard");
        setWonderGuard();

        // #031 Lightningrod -> Lightning Rod
        Utils.printProgress(totalChanges, currentChanges++, "Lightning Rod");
        setLightningRod();

        // #037 Huge Power (1.5x Attack)
        Utils.printProgress(totalChanges, currentChanges++, "Huge Power");
        setHugePower();

        // #040 Magma Armor (Water/Ground resist + 10% chance to burn on contact)
        Utils.printProgress(totalChanges, currentChanges++, "Magma Armor");
        setMagmaArmor();

        // #042 Magnet Pull
        Utils.printProgress(totalChanges, currentChanges++, "Magnet Pull");
        setMagnetPull();

        // #045 Sand Stream (+ no sandstorm damage)
        Utils.printProgress(totalChanges, currentChanges++, "Sand Stream");
        setSandStream();

        // #055 Hustle (Moves with BP 60 or under gain +1 priority)
        Utils.printProgress(totalChanges, currentChanges++, "Hustle");
        setHustle();

        // #057 Plus (4/3 ally Sp. Atk)
        Utils.printProgress(totalChanges, currentChanges++, "Plus");
        setPlus();

        // #058 Minus (4/3 ally Sp. Def)
        Utils.printProgress(totalChanges, currentChanges++, "Minus");
        setMinus();

        // #071 Arena Trap
        Utils.printProgress(totalChanges, currentChanges++, "Arena Trap");
        setArenaTrap();

        // #072 Vital Spirit (boosts Sp. Def on hit)
        Utils.printProgress(totalChanges, currentChanges++, "Vital Spirit");
        setVitalSpirit();

        // #074 Pure Power (1.5x Sp. Atk)
        Utils.printProgress(totalChanges, currentChanges++, "Pure Power");
        setPurePower();

        // #079 Rivalry (1.0x opposite gender, 1.2x same gender)
        Utils.printProgress(totalChanges, currentChanges++, "Rivalry");
        setRivalry();

        // #083 Anger Point (Boost Attack on miss, crit, or flinch)
        Utils.printProgress(totalChanges, currentChanges++, "Anger Point");
        setAngerPoint();

        // #089 Iron Fist (1.2x -> 1.3x)
        Utils.printProgress(totalChanges, currentChanges++, "Iron Fist");
        setIronFist();

        // #091 Adaptability -> Specialized
        Utils.printProgress(totalChanges, currentChanges++, "Specialized");
        setSpecialized();

        // #094 Solar Power (1.5x -> 1.3x Sp. Atk; 1/8 -> 1/10 HP per turn)
        Utils.printProgress(totalChanges, currentChanges++, "Solar Power");
        setSolarPower();

        // #102 Leaf Guard (2/3x damage received in sun)
        Utils.printProgress(totalChanges, currentChanges++, "Leaf Guard");
        setLeafGuard();

        // #105 Super Luck
        Utils.printProgress(totalChanges, currentChanges++, "Super Luck");
        setSuperLuck();

        // #115 Ice Body (+ Ice-type immunity)
        Utils.printProgress(totalChanges, currentChanges++, "Ice Body");
        setIceBody();

        // #117 Snow Warning (+ no hail damage)
        Utils.printProgress(totalChanges, currentChanges++, "Snow Warning");
        setSnowWarning();

        // #119 Frisk -> X-ray Vision
        Utils.printProgress(totalChanges, currentChanges++, "X-ray Vision");
        setXrayVision();

        // #132 Friend Guard (25% -> 20% reduction)
        Utils.printProgress(totalChanges, currentChanges++, "Friend Guard");
        setFriendGuard();

        // #134 Heavy Metal (+ 1.2x Defense, 0.9x Speed)
        Utils.printProgress(totalChanges, currentChanges++, "Heavy Metal");
        setHeavyMetal();

        // #135 Light Metal (+ 0.9x Defense, 1.2x Speed)
        Utils.printProgress(totalChanges, currentChanges++, "Light Metal");
        setLightMetal();

        // #142 Overcoat (+ immunity to spore moves)
        Utils.printProgress(totalChanges, currentChanges++, "Overcoat");
        setOvercoat();

        // #154 Justified (+ immunity to Dark-type moves)
        Utils.printProgress(totalChanges, currentChanges++, "Justified");
        setJustified();

        // #157 Sap Sipper -> Herbivore
        Utils.printProgress(totalChanges, currentChanges++, "Herbivore");
        setHerbivore();

        // #163 Turbo Blaze (Fire-type attacks are always effective)
//        setTurboblaze();


//        // NEW ABILITIES...

        // #165 Heavy Wing
        Utils.printProgress(totalChanges, currentChanges++, "Heavy Wing");
        addHeavyWing();

        // #166 Adaptability (no STAB, 1.3x for all moves)
        Utils.printProgress(totalChanges, currentChanges++, "Adaptability");
        addAdaptability();

        // #167 Insectivore
        Utils.printProgress(totalChanges, currentChanges++, "Insectivore");
        addInsectivore();

        // #168 Slush Rush
        Utils.printProgress(totalChanges, currentChanges++, "Slush Rush");
        addSlushRush();

        // #169 Prestige
        Utils.printProgress(totalChanges, currentChanges++, "Prestige");
        addPrestige();

        // #170 Lucky Foot
        Utils.printProgress(totalChanges, currentChanges++, "Lucky Foot");
        addLuckyFoot();

        // #171 Triage
        Utils.printProgress(totalChanges, currentChanges++, "Triage");
        addTriage();

        // #172 Competitive
        Utils.printProgress(totalChanges, currentChanges++, "Competitive");
        addCompetitive();

        // #173 Strong Jaw
        Utils.printProgress(totalChanges, currentChanges++, "Strong Jaw");
        addStrongJaw();

        // #174 Stamina
        Utils.printProgress(totalChanges, currentChanges++, "Stamina");
        addStamina();

        // #175 Assimilate
        Utils.printProgress(totalChanges, currentChanges++, "Assimilate");
        addAssimilate();

        // #176 Sharpness
        Utils.printProgress(totalChanges, currentChanges++, "Sharpness");
        addSharpness();

        // #177 Wind Rider
        Utils.printProgress(totalChanges, currentChanges++, "Wind Rider");
        addWindRider();

        // #178 Refrigerate
        Utils.printProgress(totalChanges, currentChanges++, "Refrigerate");
        addRefrigerate();

        // #179 Pixilate
        Utils.printProgress(totalChanges, currentChanges++, "Pixilate");
        addPixilate();

        // #180 Aerilate
        Utils.printProgress(totalChanges, currentChanges++, "Aerilate");
        addAerilate();

        // #181 Galvanize
        Utils.printProgress(totalChanges, currentChanges++, "Galvanize");
        addGalvanize();

        // #182 Gale Wings
        Utils.printProgress(totalChanges, currentChanges++, "Gale Wings");
        addGaleWings();

        // #183 Tough Claws
        Utils.printProgress(totalChanges, currentChanges++, "Tough Claws");
        addToughClaws();

        // #184 Stone Home
        Utils.printProgress(totalChanges, currentChanges, "Stone Home");
        addStoneHome();

        Utils.printProgressFinished(startTime, totalChanges);

        // Fixes ALL weather ability duration (normally it's indefinite)
        int weatherAbilityAddress = battleOvl.find("FF 20 48 71 28 1C");
        battleOvl.writeByte(weatherAbilityAddress, 0x05);

        // Changes Blaze/Torrent/Overgrow/Swarm
        setLowHpTypeBoostAbility();

        for (int i = 0; i < abilityNames.size(); ++i) {
            String abilityName = abilityNames.get(i);
            if (abilityName == null)
                throw new RuntimeException("Failed at " + i);
        }

        System.out.println("Set abilities");
    }

    private void setLimber() {
        int number = Abilities.limber;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainStatus),
                new AbilityEventHandler(Gen5BattleEventType.unknown67),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.unknown02),
                new AbilityEventHandler(Gen5BattleEventType.unknown5B, "limber_speed"),
                new AbilityEventHandler(Gen5BattleEventType.unknown5C, "limber_speed_message"));
    }

    private void setOblivious() {
        int number = Abilities.oblivious;

        // TODO Description
        String description = abilityDescriptions.get(number);

        // TODO Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(number);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainStatus),
                new AbilityEventHandler(Gen5BattleEventType.unknown67),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onPreMove, "oblivious_taunt"),
                new AbilityEventHandler(Gen5BattleEventType.unknown02));
    }

    private void setCompoundEyes() {
        int number = Abilities.compoundEyes;

        // Name
        abilityNames.set(number, "Compound Eyes");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(number)
                    .replace("Compoundeyes", "Compound Eyes");
            abilityExplanations.set(number, explanation);
        }
    }

    private void setImmunity() {
        int number = Abilities.immunity;
        abilityUpdates.put(number, "Prevents the poison status condition; NEW: Unaffected to Poison-type moves");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainStatus),
                new AbilityEventHandler(Gen5BattleEventType.unknown67),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.onPreMove, "immunity"),
                new AbilityEventHandler(Gen5BattleEventType.unknown02));
    }

    private void setShadowTag() {
        int number = Abilities.shadowTag;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreSwitch),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "shadow_tag_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "shadow_tag_message"));
    }

    private void setWonderGuard() {
        int number = Abilities.wonderGuard;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreMove),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "wonder_guard_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "wonder_guard_message"));
    }

    private void setLightningRod() {
        int number = Abilities.lightningRod;

        // Name
        abilityNames.set(number, "Lightning Rod");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(number)
                    .replace("Lightningrod", "Lightning Rod");
            abilityExplanations.set(number, explanation);
        }
    }

    private void setHugePower() {
        int number = Abilities.hugePower;

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Huge Power, huh...\uF000븁\\x0000\\xFFFEThis Ability increases a Pokémon's\\xFFFEAttack stat by half.\uF000븁\\x0000\n";
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "huge_power"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "huge_power_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "huge_power_message"));
    }

    private void setMagmaArmor() {
        int number = Abilities.magmaArmor;

        // Description
        String description = "Resists Water- and\\xFFFEGround-type moves.";
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Magma Armor, huh...\uF000븁\\x0000\\xFFFE" +
                    "This Ability halves damage from\\xFFFEWater- and Ground-type moves.\uF000븁\\x0000" +
                    "It also has a small chance to inflict\\xFFFEthe burned status condition\uF000븀\\x0000\\xFFFE" +
                    "when hit with a direct attack.\uF000븁\\x0000\\xFFFE" +
                    "What's more...\uF000븁\\x0000\\xFFFE" +
                    "It makes Eggs in your party hatch faster.\uF000븁\\x0000\n";
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onHit2, "magma_armor_burn"),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "magma_armor_resist"));
    }

    private void setMagnetPull() {
        int number = Abilities.magnetPull;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreSwitch),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "magnet_pull_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "magnet_pull_message"));
    }

    private void setSandStream() {
        int number = Abilities.sandStream;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "sand_stream_no_damage"));
    }

    private void setHustle() {
        int number = Abilities.hustle;
        abilityUpdates.put(number, "Low base power moves have increased priority");

        // Description
        String description = "Gives priority to the\\xFFFFPokémon's weaker moves.";
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Hustle, huh...\uF000븁\\x0000\\xFFFE" +
                    "Pokémon with this Ability can\\xFFFEuse weaker moves earlier\uF000븀\\x0000\\xFFFE" +
                    "than usual.\uF000븁\\x0000\\xFFFE" +
                    "What's more...\uF000븁\\x0000\\xFFFE" +
                    "It raises the chance to encounter\\xFFFEhigh-level wild Pokémon\uF000븀\\x0000\\xFFFE" +
                    "when the leading party member has it.\uF000븁\\x0000";
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetPriority, "hustle"));
    }

    private void setPlus() {
        int number = Abilities.plus;

        // Description
        abilityDescriptions.set(number, "Boosts the Sp. Atk stat\\xFFFEof allies.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "plus_spatk"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "plus_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "plus_message"));
    }

    private void setMinus() {
        int number = Abilities.minus;

        // Description
        abilityDescriptions.set(number, "Boosts the Sp. Def stat\\xFFFEof allies.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStat, "minus_spdef"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "minus_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "minus_message"));
    }

    private void setArenaTrap() {
        int number = Abilities.arenaTrap;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreSwitch),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "arena_trap_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "arena_trap_message"));
    }

    private void setVitalSpirit() {
        int number = Abilities.vitalSpirit;

        // Description
        abilityDescriptions.set(number, "Boosts the Sp. Def stat\\xFFFEwhen hit by an attack.");

        // TODO: Explanation

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onHit2, "vital_spirit"));
    }

    private void setPurePower() {
        int number = Abilities.purePower;

        // Description
        String description = abilityDescriptions.get(number)
                .replace("Attack", "Sp. Atk");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Pure Power, huh...\uF000븁\\x0000\\xFFFE" +
                    "This Ability increases a Pokémon's\\xFFFESp. Atk stat by half.\uF000븁\\x0000\n";
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "pure_power"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "pure_power_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "pure_power_message"));
    }

    private void setRivalry() {
        int number = Abilities.rivalry;

        // Name
        if (abilityExplanations != null) {
            abilityExplanations.set(number, "Rivalry, huh...\uF000븁\\x0000\\xFFFE" +
                    "This Ability raises the power of\\xFFFEthe Pokémon's move when the target is\uF000븀\\x0000\\xFFFE" +
                    "of the same gender.\uF000븁\\x0000");
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "rivalry"));
    }

    private void setAngerPoint() {
        int number = Abilities.angerPoint;

        // Description
        abilityDescriptions.set(number, "Boosts Attack when moves\\xFFFEfail or miss.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onHit2, "anger_point_crit"),
                new AbilityEventHandler(Gen5BattleEventType.onFlinch, "anger_point_flinch"),
                new AbilityEventHandler(Gen5BattleEventType.OnMoveMiss, "anger_point_miss"));
    }

    private void setIronFist() {
        setAbilityEventHandlers(Abilities.ironFist,
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "iron_fist"));
    }

    private void setSpecialized() {
        int number = ParagonLiteAbilities.specialized;

        // Name
        abilityNames.set(number, "Specialized");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.adaptability)
                    .replace("Adaptability", "Specialized");
            abilityExplanations.set(number, explanation);
        }
    }

    private void setSolarPower() {
        int number = Abilities.solarPower;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "solar_power_weather"),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "solar_power_spatk_boost"));
    }

    private void setLeafGuard() {
        int number = Abilities.leafGuard;

        // Description
        String description = "Reduces damage in\\xFFFEsunny weather.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "leaf_guard"));
    }

    private void setSuperLuck() {
        int number = Abilities.superLuck;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetCrit),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "super_luck_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "super_luck_message"));
    }

    private void setIceBody() {
        int number = Abilities.iceBody;

        // TODO: Description
        String description = abilityDescriptions.get(number);
        abilityDescriptions.set(number, description);

        // TODO: Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(number);
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onWeather),
                new AbilityEventHandler(Gen5BattleEventType.onPreMove, "ice_body_immunity"));
    }

    private void setSnowWarning() {
        int number = Abilities.snowWarning;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "snow_warning_no_damage"));
    }

    private void setXrayVision() {
        int number = ParagonLiteAbilities.xrayVision;

        // Name
        abilityNames.set(number, "X-ray Vision");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.frisk)
                    .replace("Frisk", "X-ray Vision");
            abilityExplanations.set(number, explanation);
        }

        battleEventStrings2.set(439, "\uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
        battleEventStrings2.set(440, "The wild \uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
        battleEventStrings2.set(441, "The foe's \uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
    }

    private void setFriendGuard() {
        int number = Abilities.friendGuard;

        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "friend_guard"));
    }

    private void setHeavyMetal() {
        int number = Abilities.heavyMetal;

        // Description
        String description = "Boosts the Defense stat,\\xFFFEbut lowers the Speed stat.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStat, "heavy_metal_defense"),
                new AbilityEventHandler(Gen5BattleEventType.onGetSpeedStat, "heavy_metal_speed"));
    }

    private void setLightMetal() {
        int number = Abilities.lightMetal;

        // Description
        String description = "Boosts the Speed stat, but\\xFFFElowers the Defense stat.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStat, "light_metal_defense"),
                new AbilityEventHandler(Gen5BattleEventType.onGetSpeedStat, "light_metal_speed"));
    }

    private void setOvercoat() {
        int number = Abilities.overcoat;

        // Description
        String description = "Protects the Pokémon from\\xFFFEsand, hail and powder.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onWeather),
                new AbilityEventHandler(Gen5BattleEventType.onPreMove, "overcoat_powder_immunity"));
    }

    private void setJustified() {
        int number = Abilities.justified;

        // Name
        abilityNames.set(number, "Justified");

        // Description
        String description = abilityDescriptions.get(Abilities.sapSipper)
                .replace("Grass", "Dark");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.sapSipper)
                    .replace("Sap Sipper", "Justified")
                    .replace("Grass", "Dark");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onPreMove, "justified"));
    }

    private void setHerbivore() {
        int number = ParagonLiteAbilities.herbivore;

        // Name
        abilityNames.set(number, "Herbivore");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(number)
                    .replace("Sap Sipper", "Herbivore");
            abilityExplanations.set(number, explanation);
        }
    }

    private void setTurboblaze() {
        int number = Abilities.turboblaze;

        // Description
        abilityDescriptions.set(number, "Fire-type moves\\xFFFEare always effective.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.unknown03, "turboblaze"),
                new AbilityEventHandler(Gen5BattleEventType.unknown04),
                new AbilityEventHandler(Gen5BattleEventType.unknown6A));
    }

    private void addHeavyWing() {
        int number = ParagonLiteAbilities.heavyWing;
        abilityUpdates.put(number, "Powers up Flying-type moves by 1.5x");

        // Name
        abilityNames.set(number, "Heavy Wing");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist)
                .replace("punching", "Flying-type");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Heavy Wing")
                    .replace("moves that punch", "Flying-type moves");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "heavy_wing"));
    }

    private void addAdaptability() {
        int number = ParagonLiteAbilities.adaptability;
        abilityUpdates.put(number, "NEW: Moves of any type receive STAB at 1.3x damage (see \"Specialized\" for former Adaptability effect)");

        // Name
        abilityNames.set(number, "Adaptability");

        // Description
        String description = "Powers up moves of all\\xFFFEtypes equally.";
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Adaptability, huh...\uF000븁\\x0000\\xFFFE" +
                    "This ability gives a power boost to\\xFFFEall moves, even if the move doesn't\uF000븀\\x0000\\xFFFE" +
                    "match the Pokémon's type.\uF000븁\\x0000";
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "adaptability"));
    }

    private void addInsectivore() {
        int index = ParagonLiteAbilities.insectivore;

        // Name
        abilityNames.set(index, "Insectivore");

        // Description
        String description = abilityDescriptions.get(Abilities.waterAbsorb)
                .replace("Water", "Bug");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.waterAbsorb)
                    .replace("Water Absorb", "Insectivore")
                    .replace("Water", "Bug");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onPreMove, "insectivore"));
    }

    private void addSlushRush() {
        int index = ParagonLiteAbilities.slushRush;

        // Name
        abilityNames.set(index, "Slush Rush");

        // Description
        String description = abilityDescriptions.get(Abilities.snowCloak)
                .replace("evasion", "Speed");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.snowCloak)
                    .replace("Snow Cloak", "Slush Rush")
                    .replace("evasiveness", "Speed");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetSpeedStat, "slush_rush_speed"),
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "slush_rush_weather_immune"));
    }

    private void addPrestige() {
        int index = ParagonLiteAbilities.prestige;

        // Name
        abilityNames.set(index, "Prestige");

        // Description
        String description = abilityDescriptions.get(Abilities.moxie)
                .replace("Attack", "Sp. Atk");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.moxie)
                    .replace("Moxie", "Prestige")
                    .replace("Attack", "Sp. Atk");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onPostHit2, "prestige"));
    }

    private void addLuckyFoot() {
        int index = ParagonLiteAbilities.luckyFoot;

        // Name
        abilityNames.set(index, "Lucky Foot");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist)
                .replace("punching", "kicking");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Lucky Foot")
                    .replace("punch", "kick");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "lucky_foot"));
    }

    private void addTriage() {
        int index = ParagonLiteAbilities.triage;

        // Name
        abilityNames.set(index, "Triage");

        // Description
        String description = abilityDescriptions.get(Abilities.prankster)
                .replace("status", "healing");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.prankster)
                    .replace("Prankster", "Triage")
                    .replace("status moves", "moves that heal");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetPriority, "triage"));
    }

    private void addCompetitive() {
        int index = ParagonLiteAbilities.competitive;

        // Name
        abilityNames.set(index, "Competitive");

        // Description
        String description = abilityDescriptions.get(Abilities.defiant)
                .replace("Attack", "Sp. Atk");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.defiant)
                    .replace("Defiant", "Competitive")
                    .replace("Attack", "Sp. Atk");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onPostStatChange, "competitive"));
    }

    private void addStrongJaw() {
        int index = ParagonLiteAbilities.strongJaw;

        // Name
        abilityNames.set(index, "Strong Jaw");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist)
                .replace("punching", "biting");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Strong Jaw")
                    .replace("moves that punch", "moves that bite");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "strong_jaw"));
    }

    private void addStamina() {
        int index = ParagonLiteAbilities.stamina;

        // Name
        abilityNames.set(index, "Stamina");

        // Description
        abilityDescriptions.set(index, "Boosts the Defense stat\\xFFFEwhen hit by an attack.");

        // TODO: Explanation
        if (abilityExplanations != null) {
            abilityExplanations.set(index, "A");
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onHit2, "stamina"));
    }

    private void addAssimilate() {
        int index = ParagonLiteAbilities.assimilate;

        // Name
        abilityNames.set(index, "Assimilate");

        // Description
        String description = abilityDescriptions.get(Abilities.sapSipper)
                .replace("Grass", "Psychic")
                .replace("Attack", "Sp. Atk");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.sapSipper)
                    .replace("Sap Sipper", "Assimilate")
                    .replace("Grass", "Psychic")
                    .replace("Attack", "Sp. Atk");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onPreMove, "assimilate"));
    }

    private void addSharpness() {
        int index = ParagonLiteAbilities.sharpness;

        // Name
        abilityNames.set(index, "Sharpness");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist)
                .replace("punching", "slicing");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Sharpness")
                    .replace("moves that punch", "moves that slice");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "sharpness"));
    }

    private void addWindRider() {
        int index = ParagonLiteAbilities.windRider;

        // Name
        abilityNames.set(index, "Wind Rider");

        // Description
        String description = "Boosts Attack when hit\\xFFFEby a wind move.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        if (abilityExplanations != null) {
            String explanation = "";
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onPreMove, "wind_rider"));
    }

    private void addRefrigerate() {
        int index = ParagonLiteAbilities.refrigerate;

        // Name
        abilityNames.set(index, "Refrigerate");

        // Description
        String description = "Normal-type moves become\\xFFFEIce-type moves.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        if (abilityExplanations != null) {
            String explanation = "";
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "refrigerate_type"),
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "common_move_type_change_power"));
    }

    private void addPixilate() {
        int index = ParagonLiteAbilities.pixilate;

        // Name
        abilityNames.set(index, "Pixilate");

        // Description
        String description = "Normal-type moves become\\xFFFEFairy-type moves.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        if (abilityExplanations != null) {
            String explanation = "";
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "pixilate_type"),
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "common_move_type_change_power"));
    }

    private void addAerilate() {
        int index = ParagonLiteAbilities.aerilate;

        // Name
        abilityNames.set(index, "Aerilate");

        // Description
        String description = "Normal-type moves become\\xFFFEFlying-type moves.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        if (abilityExplanations != null) {
            String explanation = "";
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "aerilate_type"),
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "common_move_type_change_power"));
    }

    private void addGalvanize() {
        int index = ParagonLiteAbilities.galvanize;

        // Name
        abilityNames.set(index, "Galvanize");

        // Description
        String description = "Normal-type moves become\\xFFFEElectric-type moves.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        if (abilityExplanations != null) {
            String explanation = "";
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "galvanize_type"),
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "common_move_type_change_power"));
    }

    private void addGaleWings() {
        int index = ParagonLiteAbilities.galeWings;

        // Name
        abilityNames.set(index, "Gale Wings");

        // Description
        String description = "Gives priority to Flying-type\\xFFFEmoves at full HP.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        if (abilityExplanations != null) {
            String explanation = "";
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetPriority, "gale_wings"));
    }

    private void addToughClaws() {
        int index = ParagonLiteAbilities.toughClaws;

        // Name
        abilityNames.set(index, "Tough Claws");

        // Description
        String description = "Powers up moves that\\xFFFEmake direct contact.";
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Iron Fist, huh...\uF000븁\\x0000\\xFFFE" +
                    "This Ability increases the power of\\xFFFEmoves that make direct contact\uF000븀\\x0000\\xFFFE" +
                    "with the target.";
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "tough_claws"));
    }

    private void addStoneHome() {
        int number = ParagonLiteAbilities.stoneHome;

        // Name
        abilityNames.set(number, "Stone Home");

        // Description
        abilityDescriptions.set(number, "Boosts the Defense stat\\xFFFEof allies.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStat, "stone_home_defense"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "stone_home_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "stone_home_message"));
    }

    private void setLowHpTypeBoostAbility() {
        int ramAddress = battleOvl.find("F8 B5 06 1C 03 20 0D 1C 14 1C 03 27");

        List<String> lines = readLines("eventhandlers/ability/low_hp_type_boost.s");

        ArmParser armParser = new ArmParser(globalAddressMap);
        byte[] data = armParser.parse(lines, battleOvl, ramAddress);

        int maxSize = 72;
        if (data.length > maxSize)
            throw new RuntimeException("Exceeded size!");

        battleOvl.writeBytes(ramAddress, data);
    }

    public void setMoves() {
        registerMoveEffects();

        // Name Updates
        moves[Moves.viseGrip].name = "Vise Grip"; // 011
        moves[Moves.feintAttack].name = "Feint Attack"; // 185
        moves[Moves.smellingSalts].name = "Smelling Salts"; // 265

        // TODO: These won't matter after Fairy is properly implemented
        moves[Moves.submission].name = "Play Rough"; // 066
        moves[Moves.lusterPurge].name = "Dazzling Gleam"; // 295
        moves[Moves.mistBall].name = "Moonblast"; // 296

        // Kick Moves
        moves[Moves.stomp].isCustomKickMove = true; // 023
        moves[Moves.doubleKick].isCustomKickMove = true; // 024
        moves[Moves.jumpKick].isCustomKickMove = true; // 026
        moves[Moves.rollingKick].isCustomKickMove = true; // 27
        moves[Moves.lowKick].isCustomKickMove = true; // 67
        moves[Moves.highJumpKick].isCustomKickMove = true; // 136
        moves[Moves.tripleKick].isCustomKickMove = true; // 167
        moves[Moves.blazeKick].isCustomKickMove = true; // 299
        moves[Moves.lowSweep].isCustomKickMove = true; // 490

        // Bite Moves
        moves[Moves.bite].isCustomBiteMove = true; // 044
        moves[Moves.hyperFang].isCustomBiteMove = true; // 158
        moves[Moves.crunch].isCustomBiteMove = true; // 242
        moves[Moves.poisonFang].isCustomBiteMove = true; // 305
        moves[Moves.thunderFang].isCustomBiteMove = true; // 422
        moves[Moves.iceFang].isCustomBiteMove = true; // 423
        moves[Moves.fireFang].isCustomBiteMove = true; // 424

        // Slice Moves
        moves[Moves.cut].isCustomSliceMove = true; // 015
        moves[Moves.razorLeaf].isCustomSliceMove = true; // 075
        moves[Moves.slash].isCustomSliceMove = true; // 163
        moves[Moves.furyCutter].isCustomSliceMove = true; // 210
        moves[Moves.airCutter].isCustomSliceMove = true; // 314
        moves[Moves.aerialAce].isCustomSliceMove = true; // 332
        moves[Moves.leafBlade].isCustomSliceMove = true; // 348
        moves[Moves.airSlash].isCustomSliceMove = true; // 403
        moves[Moves.crossPoison].isCustomSliceMove = true; // 440
        moves[Moves.nightSlash].isCustomSliceMove = true; // 400
        moves[Moves.xScissor].isCustomSliceMove = true; // 404
        moves[Moves.psychoCut].isCustomSliceMove = true; // 427
        moves[Moves.sacredSword].isCustomSliceMove = true; // 533
        moves[Moves.razorShell].isCustomSliceMove = true; // 534
        moves[Moves.secretSword].isCustomSliceMove = true; // 548

        // Triage
        for (int i = 0; i <= Gen5Constants.moveCount; ++i) {
            if (moves[i] == null)
                continue;

            if (moves[i].isHealMove || moves[i].recoil > 0)
                moves[i].isCustomTriageMove = true;
        }

        // Powder moves
        moves[Moves.poisonPowder].isCustomPowderMove = true; // 077
        moves[Moves.stunSpore].isCustomPowderMove = true; // 078
        moves[Moves.sleepPowder].isCustomPowderMove = true; // 079
        moves[Moves.spore].isCustomPowderMove = true; // 147
        moves[Moves.cottonSpore].isCustomPowderMove = true; // 178
        moves[Moves.ragePowder].isCustomPowderMove = true; // 476

        // Wind moves
        moves[Moves.razorWind].isCustomWindMove = true; // 013
        moves[Moves.gust].isCustomWindMove = true; // 016
        moves[Moves.whirlwind].isCustomWindMove = true; // 018
        moves[Moves.blizzard].isCustomWindMove = true; // 059
        moves[Moves.aeroblast].isCustomWindMove = true; // 177
        moves[Moves.icyWind].isCustomWindMove = true; // 196
        moves[Moves.twister].isCustomWindMove = true; // 239
        moves[Moves.heatWave].isCustomWindMove = true; // 257
        moves[Moves.airCutter].isCustomWindMove = true; // 314
        moves[Moves.silverWind].isCustomWindMove = true; // 318
        moves[Moves.tailwind].isCustomWindMove = true; // 366
        moves[Moves.ominousWind].isCustomWindMove = true; // 406
        moves[Moves.hurricane].isCustomWindMove = true; // 542


        Scanner sc;
        try {
            sc = new Scanner(FileFunctions.openConfig("paragonlite/moves.ini"), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Move move = null;
        while (sc.hasNextLine()) {
            String q = sc.nextLine().trim();
            if (q.contains("#")) {
                q = q.substring(0, q.indexOf("#")).trim();
            }

            if (q.isEmpty())
                continue;

            if (q.startsWith("[") && q.endsWith("]")) {
                // New move
                String numStr = q.substring(1, 4);
                int num = Integer.parseInt(numStr);
                move = moves[num];

                String nameStr = q.substring(5, q.length() - 1).trim();

                // validate move
                String regex = "[- ]";
                String moveNameCheck = move.name.replaceAll(regex, "");
                String nameStrCheck = nameStr.replaceAll(regex, "");
                if (!moveNameCheck.equalsIgnoreCase(nameStrCheck)) {
                    throw new RuntimeException(String.format("Move names didn't match: %s and %s", move.name, nameStr));
                }

                continue;
            }

            if (move == null) {
                System.err.println("no move at " + q);
                continue;
            }

            String[] r = q.split("=", 2);
            if (r.length == 1) {
                System.err.println("invalid entry " + q);
                continue;
            }

            if (r[1].endsWith("\r\n")) {
                r[1] = r[1].substring(0, r[1].length() - 2);
            }

            String key = r[0].trim();
            String value = r[1].trim();

            switch (key) {
                case "Name": {
                    move.name = value;
                    break;
                }
                case "Description": {
                    move.description = value;
                    break;
                }
                case "Type": {
                    move.type = Type.valueOf(value);
                    break;
                }
                case "Qualities": {
                    move.qualities = MoveQualities.valueOf(value);
                    break;
                }
                case "Category": {
                    move.category = MoveCategory.valueOf(value);
                    if (move.hasPerfectAccuracy())
                        move.accuracy = Move.getPerfectAccuracy();

                    break;
                }
                case "Power": {
                    move.power = Integer.parseInt(value);
                    break;
                }
                case "Accuracy": {
                    if (value.equals("--")) {
                        move.accuracy = Move.getPerfectAccuracy();
                        break;
                    }

                    move.accuracy = Integer.parseInt(value);
                    break;
                }
                case "PP": {
                    move.pp = Integer.parseInt(value);
                    break;
                }
                case "Priority": {
                    move.priority = Integer.parseInt(value);
                    break;
                }
                case "Hits": {
                    if (value.equals("[]")) {
                        move.minHits = 0;
                        move.maxHits = 0;
                        break;
                    }

                    if (value.startsWith("[") && value.endsWith("]")) {
                        String[] values = value.substring(1, value.length() - 1).split(",", 2);
                        move.minHits = Integer.parseInt(values[0].trim());
                        move.maxHits = Integer.parseInt(values[1].trim());
                        break;
                    }

                    int hits = Integer.parseInt(value);
                    move.minHits = hits;
                    move.maxHits = hits;
                    break;
                }
                case "StatusType": {
                    move.statusType = MoveStatusType.valueOf(value);
                    if (move.statusType == MoveStatusType.NONE)
                        move.statusPercentChance = 0;

                    break;
                }
                case "StatusChance": {
                    move.statusPercentChance = Integer.parseInt(value);
                    break;
                }
                case "CriticalChance": {
                    move.criticalChance = CriticalChance.valueOf(value);
                    break;
                }
                case "FlinchChance": {
                    move.flinchPercentChance = Integer.parseInt(value);
                    break;
                }
                case "Effect": {
                    move.effect = MoveEffect.valueOf(value);
                    break;
                }
                case "Recoil": {
                    move.recoil = Integer.parseInt(value);
                    break;
                }
                case "Heal": {
                    move.heal = Integer.parseInt(value);
                    break;
                }
                case "Target": {
                    move.target = MoveTarget.valueOf(value);
                    break;
                }
                case "StatChanges": {
                    if (!value.startsWith("[") || !value.endsWith("]"))
                        throw new RuntimeException();

                    String[] values = value.substring(1, value.length() - 1).split(",");

                    for (int i = 0; i < 3; ++i) {
                        if (i >= values.length) {
                            move.statChanges[i] = new Move.StatChange();
                            continue;
                        }

                        int defaultChance = move.category == MoveCategory.STATUS ? 0 : 100;
                        move.statChanges[i] = new Move.StatChange(values[i].trim(), defaultChance);
                    }

                    break;
                }
                case "IsStolenBySnatch": {
                    switch (value) {
                        case "TRUE":
                            move.isStolenBySnatch = true;
                            break;
                        case "FALSE":
                            move.isStolenBySnatch = false;
                            break;
                        default:
                            throw new RuntimeException(String.format("Unknown flag value for %s: \"%s\"", key, value));
                    }

                    break;
                }
                case "IsCopiedByMirrorMove": {
                    switch (value) {
                        case "TRUE":
                            move.isCopiedByMirrorMove = true;
                            break;
                        case "FALSE":
                            move.isCopiedByMirrorMove = false;
                            break;
                        default:
                            throw new RuntimeException(String.format("Unknown flag value for %s: \"%s\"", key, value));
                    }

                    break;
                }
                case "HitsThroughSubstitute": {
                    switch (value) {
                        case "TRUE":
                            move.hitsThroughSubstitute = true;
                            break;
                        case "FALSE":
                            move.hitsThroughSubstitute = false;
                            break;
                        default:
                            throw new RuntimeException(String.format("Unknown flag value for %s: \"%s\"", key, value));
                    }

                    break;
                }
                default:
                    throw new RuntimeException(String.format("Unknown parameter \"%s\"", key));
            }
        }

        int newMoves = 2; // #310 Astonish, #121 Egg Bomb

        int[] movesToClear = {
                Moves.dragonRage, // #082
                Moves.nightShade, // #101
                Moves.psywave, // #149
                Moves.rollout, // #205
                Moves.memento, // #262
                Moves.iceBall, // #301
                Moves.chatter, // #448
        };

        for (int moveToClear : movesToClear) {
            clearMoveEventHandlers(moveToClear);
        }

        // TODO: For some reason the relocator addresses values are wrong
        relocateMoveListRamAddress(newMoves - movesToClear.length);

//        // #018 Whirlwind
//        setMoveData(Moves.whirlwind, new MoveEventHandler(Gen5BattleEventType.onPostHit, Moves.circleThrow));

        // + #310 Astonish
        cloneMoveEventHandlers(Moves.astonish, Moves.fakeOut);

        // + #121 Egg Bomb
        cloneMoveEventHandlers(Moves.eggBomb, Moves.psystrike);

        // #167 Triple Kick
        setMoveEventHandlers(Moves.tripleKick, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "triple_kick"));

        // #200 Outrage
        cloneMoveEventHandlers(Moves.outrage, Moves.revenge);

        // #237 Hidden Power
        setMoveEventHandlers(Moves.hiddenPower, new MoveEventHandler(Gen5BattleEventType.onGetMoveType));

        // #243 Mirror Coat
        cloneMoveEventHandlers(Moves.mirrorCoat, Moves.eruption);

        // #360 Gyro Ball
        setMoveEventHandlers(Moves.gyroBall, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "gyro_ball"));

        // #368 Metal Burst
        cloneMoveEventHandlers(Moves.metalBurst, Moves.eruption);

        // #381 Lucky Chant
        cloneMoveEventHandlers(Moves.luckyChant, Moves.focusEnergy);

        // #449 Judgment
        setMoveEventHandlers(Moves.judgment, new MoveEventHandler(Gen5BattleEventType.onGetMoveType, "judgment"));

        // #486 Electro Ball
        setMoveEventHandlers(Moves.electroBall, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "electro_ball"));

        System.out.println("Set moves");
    }

    public void setItems() {
        registerItemEffects();

        updateNaturalGiftPowers();

        relocateItemListRamAddress(9);

        // Weather change item
        List<String> weatherRockCommonLines = readLines("eventhandlers/item/common_weather_change_item.s");
        battleOvl.writeCode(weatherRockCommonLines, "CommonWeatherChangeItem");

        // #282 Icy Rock
        setIcyRock();

        // #283 Smooth Rock
        setSmoothRock();

        // #284 Heat Rock
        setHeatRock();

        // #285 Damp Rock
        setDampRock();

        /* NEW */

        // #113 Weakness Policy
        addWeaknessPolicy();

        // #114 Assault Vest
        addAssaultVest();

        // #115 Pixie Plate
        addPixiePlate();

        // #122 Roseli Berry
        addRoseliBerry();

        // #125 Fairy Gem
        addFairyGem();

        // #518 Blank Plate
        addBlankPlate();

        // #521 Clear Amulet
        addClearAmulet();

        // #524 Covert Cloak
        addCovertCloak();

        // #525 Loaded Dice
        addLoadedDice();

        System.out.println("Set items");
    }

    // In Gen VI, all Berries got a +20 boost to power for Natural Gift across the board.
    // We've decided to make this +30 (or +10 to Gen VI+ power) as it's a one-time use move.
    // This means berries like Cheri or Oran now do 90 (originally 60 in Gen V and 80 in Gen VI+)
    // and berries like Liechi or Rowap now do 110 (originally 80 and 100)
    void updateNaturalGiftPowers() {
        for (byte[] itemData : itemDataNarc.files) {
            if (itemData[0x07] > 0)
                itemData[0x07] += 30;
        }
    }

    void setIcyRock() {
        int index = Items.icyRock;

        itemDescriptions.set(index, "An item to be held by a Pokémon.\\xFFFEThis rock summons a hailstorm\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(index,
                new ItemEventHandler(Gen5BattleEventType.onPokeEnter, "icy_rock_hail"),
                new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "icy_rock_hail"),
                new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "icy_rock_immune"));
    }

    void setSmoothRock() {
        int number = Items.smoothRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock summons a sandstorm\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onPokeEnter, "smooth_rock_sandstorm"),
                new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "smooth_rock_sandstorm"),
                new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "smooth_rock_immune"));
    }

    void setHeatRock() {
        int number = Items.heatRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock turns the sunlight harsh\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onPokeEnter, "heat_rock"),
                new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "heat_rock"));
    }

    void setDampRock() {
        int number = Items.dampRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock makes it rain\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onPokeEnter, "damp_rock"),
                new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "damp_rock"));
    }

    void addWeaknessPolicy() {
        int index = ParagonLiteItems.weaknessPolicy;

        setItemName(index, "Weakness Policy", "Weakness Policies");
        itemDescriptions.set(index, "An item to be held by a Pokémon. Attack\\xFFFEand Sp. Atk sharply increase if the\\xFFFEholder is hit with a move it's weak to.");

        setItemPocket(index, Item.Pocket.ITEMS);
        setItemPrice(index, 100);
        setItemBattleEffect(index, 147);
        setItemFlingPower(index, 80);
        setItemNaturalGiftType(index, null);
        setItemIsOneTimeUse(index, true);
        setItemType(index, Item.ItemType.HELD);

        setItemSprite(index, "weakness_policy");

        setItemEventHandlers(index,
                new ItemEventHandler(Gen5BattleEventType.onHit2, "weakness_policy_on_hit"),
                new ItemEventHandler(Gen5BattleEventType.unknown72, "weakness_policy_boost"));
    }

    void addAssaultVest() {
        int index = ParagonLiteItems.assaultVest;

        setItemName(index, "Assault Vest", "Assault Vests");
        itemDescriptions.set(index, "An item to be held by a Pokémon. This\\xFFFEvest boosts the holder's Sp. Def stat\\xFFFEbut prevents the use of status moves.");

        setItemPocket(index, Item.Pocket.ITEMS);
        setItemPrice(index, 100);
        setItemBattleEffect(index, 147);
        setItemFlingPower(index, 80);
        setItemNaturalGiftType(index, null);
        setItemType(index, Item.ItemType.HELD);

        setItemSprite(index, "assault_vest");
    }

    void addPixiePlate() {
        int index = ParagonLiteItems.pixiePlate;

        setItemName(index, "Pixie Plate", "Pixie Plates");
        itemDescriptions.set(index, "An item to be held by a Pokémon.\\xFFFEIt is a stone tablet that boosts the\\xFFFEpower of Fairy-type moves.");

        setItemPocket(index, Item.Pocket.ITEMS);
        setItemPrice(index, 1000);
        setItemBattleEffect(index, 147); // TODO
        setItemValueVar(index, 20); // 1.2x
        setItemFlingPower(index, 90);
        setItemNaturalGiftType(index, null);
        setItemType(index, Item.ItemType.HELD);

        setItemSprite(index, Items.flamePlate, "pixie_plate");

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "pixie_plate"));
    }

    void addRoseliBerry() {
        int index = ParagonLiteItems.roseliBerry;

        setItemName(index, "Roseli Berry", "Roseli Berries");
        itemDescriptions.set(index, "Weakens a supereffective Fairy-type\\xFFFEattack against the holding Pokémon.");

        setItemPocket(index, Item.Pocket.BERRIES);
        setItemPrice(index, 100);
        setItemBattleEffect(index, 147); // TODO
        setItemFlingPower(index, 10);
        setItemNaturalGiftPower(index, 90);
        setItemNaturalGiftType(index, Type.FAIRY);
        setItemIsOneTimeUse(index, true);

        setItemSprite(index, "roseli_berry");

        setItemEventHandlers(index,
                new ItemEventHandler(Gen5BattleEventType.onGetDamage, "roseli_berry_super_effective_check"),
                new ItemEventHandler(Gen5BattleEventType.unknown44, Items.occaBerry));
    }

    void addFairyGem() {
        int index = ParagonLiteItems.fairyGem;

        setItemName(index, "Fairy Gem", "Fairy Gems");
        itemDescriptions.set(index, "A gem with an essence of fairies. When\\xFFFEheld, it strengthens the power of a\\xFFFEFairy-type move only once.");

        setItemPocket(index, Item.Pocket.ITEMS);
        setItemPrice(index, 200);
        setItemBattleEffect(index, 147); // TODO
        setItemFlingPower(index, 0);
        setItemNaturalGiftType(index, null);
        setItemIsOneTimeUse(index, true);

        setItemSprite(index, Items.fireGem, "fairy_gem");

        setItemEventHandlers(index,
                new ItemEventHandler(Gen5BattleEventType.unknown81, "fairy_gem_work"),
                new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "fairy_gem_damage_boost"),
                new ItemEventHandler(Gen5BattleEventType.unknown88, Items.fireGem));
    }

    void addBlankPlate() {
        int index = ParagonLiteItems.blankPlate;

        setItemName(index, "Blank Plate", "Blank Plates");
        itemDescriptions.set(index, "An item to be held by a Pokémon.\\xFFFEIt is a stone tablet that boosts the\\xFFFEpower of Normal-type moves.");

        setItemPocket(index, Item.Pocket.ITEMS);
        setItemPrice(index, 1000);
        setItemBattleEffect(index, 147); // TODO
        setItemValueVar(index, 20); // 1.2x
        setItemFlingPower(index, 90);
        setItemNaturalGiftType(index, null);
        setItemType(index, Item.ItemType.HELD);

        setItemSprite(index, Items.flamePlate, "blank_plate");

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "blank_plate"));
    }

    void addClearAmulet() {
        int index = ParagonLiteItems.clearAmulet;

        setItemName(index, "Clear Amulet", "Clear Amulets");
        itemDescriptions.set(index, "An item to be held by a Pokémon.\\xFFFEThis amulet prevents other Pokémon\\xFFFEfrom lowering the holder's stats.");

        setItemPocket(index, Item.Pocket.ITEMS);
        setItemPrice(index, 100);
        setItemBattleEffect(index, 147);
        setItemFlingPower(index, 30);
        setItemNaturalGiftType(index, null);
        setItemType(index, Item.ItemType.HELD);

        setItemSprite(index, "clear_amulet");

        setItemEventHandlers(index,
                new ItemEventHandler(0x5B, "clear_amulet_5B"),
                new ItemEventHandler(0x5C, "clear_amulet_5C"));
    }

    void addCovertCloak() {
        int index = ParagonLiteItems.covertCloak;

        setItemName(index, "Covert Cloak", "Covert Cloaks");
        itemDescriptions.set(index, "An item to be held by a Pokémon. This\\xFFFEcloak conceals the holder, protecting\\xFFFEit from the additional effects of moves.");

        setItemPocket(index, Item.Pocket.ITEMS);
        setItemPrice(index, 100);
        setItemBattleEffect(index, 147);
        setItemFlingPower(index, 30);
        setItemNaturalGiftType(index, null);
        setItemType(index, Item.ItemType.HELD);

        setItemSprite(index, "covert_cloak");

        cloneItemEventHandlersFromAbility(index, Abilities.shieldDust);
    }

    void addLoadedDice() {
        int index = ParagonLiteItems.loadedDice;

        setItemName(index, "Loaded Dice", "Loaded Dice");
        itemDescriptions.set(index, "An item to be held by a Pokémon.\\xFFFEIt always rolls a good number, ensuring\\xFFFEthat multistrike moves hit more times.");

        setItemPocket(index, Item.Pocket.ITEMS);
        setItemPrice(index, 100);
        setItemBattleEffect(index, 147);
        setItemFlingPower(index, 30);
        setItemNaturalGiftType(index, null);
        setItemType(index, Item.ItemType.HELD);

        setItemSprite(index, "loaded_dice");

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onGetMoveHits, "loaded_dice"));
    }

    private void setItemName(int itemNumber, String name, String pluralName) {
        itemNames.set(itemNumber, name);
        itemNameMessages.set(itemNumber, "\uF000봁\\x0000a \uF000\uFF00\\x0001ÿ" + name);
        itemPluralNames.set(itemNumber, pluralName);
    }

    private void setItemPocket(int itemNumber, Item.Pocket pocket) {
        if (romHandler.isWhite() || romHandler.isBlack())
            // TODO: Item pockets are seemingly somewhere else in BW?
            return;

        ParagonLiteAddressMap.AddressBase addressBase = globalAddressMap.getAddressData(arm9, "Data_ItemPockets");
        arm9.writeByte(addressBase.address + itemNumber, (byte) pocket.ordinal());
    }

    // 0x00
    private void setItemPrice(int itemNumber, int price) {
        if (price % 10 != 0)
            throw new RuntimeException("Price must be a multiple of 10");

        byte[] data = itemDataNarc.files.get(itemNumber);
        writeHalf(data, 0x00, price / 10);
    }

    // 0x02
    private void setItemBattleEffect(int itemNumber, int battleEffectId) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x02] = (byte) battleEffectId;
    }

    // 0x03
    private void setItemValueVar(int itemNumber, int value) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x03] = (byte) value;
    }

    // 0x06
    private void setItemFlingPower(int itemNumber, int power) {
        setItemFlingData(itemNumber, power, 0);
    }

    // 0x05, 0x06
    private void setItemFlingData(int itemNumber, int power, int effect) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x05] = (byte) effect;
        data[0x06] = (byte) power;
    }

    // 0x07
    private void setItemNaturalGiftPower(int itemNumber, int power) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x07] = (byte) power;
    }

    // 0x08 [0-4]
    private void setItemNaturalGiftType(int itemNumber, Type type) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        if (type == null)
            data[0x08] |= (byte) 0b00011111;
    }

    // 0x08 [6]
    private void setCanRegister(int itemNumber, boolean canRegister) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        if (canRegister)
            data[0x08] |= (byte) 0b01000000;
        else
            data[0x08] &= (byte) 0b10111111;
    }

    // 0x0A
    private void setOverworldUseEffect(int itemNumber, int effectNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x0A] = (byte) effectNumber;
    }

    // 0x0E
    private void setItemIsOneTimeUse(int itemNumber, boolean isOneTimeUse) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x0E] = (byte) (isOneTimeUse ? 1 : 0);
    }

    // 0x0D
    private void setItemType(int itemNumber, Item.ItemType itemType) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x0D] = (byte) itemType.ordinal();
    }

    public void test() {
        System.out.println("- test");

        disableRandomness();

//        // TODO: Frostbite 1/6 HP
//        battleOvl.writeByte(0x021BBB88, 64);

        List<Trainer> trainers = romHandler.getTrainers();

        for (Trainer tr : trainers) {
            tr.setPokemonHaveCustomMoves(true);
            tr.setPokemonHaveItems(true);

            TrainerPokemon poke1 = tr.pokemon.get(0);
            poke1.pokemon = romHandler.getPokemon().get(Species.emolga);
            poke1.level = 37;
            pokes[Species.emolga].ability1 = Abilities.motorDrive;
            poke1.abilitySlot = 1;
            poke1.moves = new int[]{Moves.airSlash, Moves.discharge, Moves.energyBall, Moves.tailwind};
            poke1.IVs = 30;
            poke1.heldItem = Items.focusSash;

            if (tr.pokemon.size() < 2)
                tr.pokemon.add(tr.pokemon.get(0).copy());
            TrainerPokemon poke2 = tr.pokemon.get(1);
            poke2.pokemon = romHandler.getPokemon().get(Species.lanturn);
            poke2.level = 37;
            pokes[Species.lanturn].ability1 = Abilities.voltAbsorb;
            poke2.abilitySlot = 1;
            poke2.moves = new int[]{Moves.muddyWater, Moves.icyWind, Moves.discharge, Moves.tailGlow};
            poke1.IVs = 30;
            poke2.heldItem = Items.rindoBerry;

            if (tr.pokemon.size() < 3)
                tr.pokemon.add(tr.pokemon.get(0).copy());
            TrainerPokemon poke3 = tr.pokemon.get(2);
            poke3.pokemon = romHandler.getPokemon().get(Species.electivire);
            pokes[Species.electivire].secondaryType = Type.FIGHTING;
            poke3.level = 37;
            pokes[Species.electivire].ability1 = Abilities.motorDrive;
            poke3.abilitySlot = 1;
            poke3.moves = new int[]{Moves.discharge, Moves.closeCombat, Moves.rockSlide, Moves.icePunch};
            poke3.IVs = 30;
            poke3.heldItem = Items.shucaBerry;

            if (tr.pokemon.size() < 4)
                tr.pokemon.add(tr.pokemon.get(0).copy());
            TrainerPokemon poke4 = tr.pokemon.get(3);
            poke4.pokemon = romHandler.getPokemon().get(Species.raichu);
            poke4.level = 37;
            pokes[Species.raichu].ability1 = Abilities.lightningRod;
            poke4.abilitySlot = 1;
            poke4.moves = new int[]{Moves.grassKnot, Moves.extremeSpeed, Moves.thunderbolt, Moves.icicleCrash};
            poke4.IVs = 30;
            poke4.heldItem = Items.airBalloon;

            if (tr.pokemon.size() < 5)
                tr.pokemon.add(tr.pokemon.get(0).copy());
            TrainerPokemon poke5 = tr.pokemon.get(4);
            poke5.pokemon = romHandler.getPokemon().get(Species.stunfisk);
            poke5.level = 37;
            pokes[Species.stunfisk].ability1 = Abilities.lightningRod;
            poke5.abilitySlot = 1;
            poke5.moves = new int[]{Moves.muddyWater, Moves.discharge, Moves.earthPower, Moves.sludgeBomb};
            poke5.IVs = 30;
            poke5.heldItem = Items.sitrusBerry;

            if (tr.pokemon.size() < 6)
                tr.pokemon.add(tr.pokemon.get(0).copy());
            TrainerPokemon poke6 = tr.pokemon.get(5);
            poke6.pokemon = romHandler.getPokemon().get(Species.zebstrika);
            poke6.level = 38;
            pokes[Species.zebstrika].ability1 = Abilities.motorDrive;
            poke6.abilitySlot = 1;
            poke6.moves = new int[]{Moves.wildCharge, Moves.flareBlitz, Moves.earthquake, Moves.xScissor};
            poke6.IVs = 30;
            poke6.heldItem = Items.lifeOrb;

//            tr.pokemon.set(0, poke2);
//            tr.pokemon.set(1, poke1);
        }

        romHandler.setTrainers(trainers, true, true);

//        // Set debug AI Flag
//        for (Trainer tr : trainers) {
//            tr.aiFlags = 1 << 14;
//        }
//        romHandler.setTrainers(trainers, false, false);
    }

    private void researchGetEffectVals(int listCount, int listAddress) {
        List<String> strs = new ArrayList<>(listCount);
        for (int i = 0; i < listCount; ++i) {
            StringBuilder sb = new StringBuilder();

            int address = listAddress + i * 8;
            int moveNumber = battleOvl.readWord(address);

            sb.append(moveNumber);
            sb.append('\t');
            sb.append(String.format("0x%04X", moveNumber));
            sb.append('\t');
            sb.append(address);
            sb.append('\t');
            sb.append(String.format("0x%08X", address));
            sb.append('\t');

            int redirectorAddress = battleOvl.readWord(address + 4) - 1;

            int eventHandlerListCount = 0;
            int eventHandlerListAddress = 0;

            if (redirectorAddress > -1) {
                eventHandlerListCount = getEventHandlerListCountFromRedirector(redirectorAddress);
                eventHandlerListAddress = getEventHandlerListAddressFromRedirector(redirectorAddress);
            }

            sb.append(eventHandlerListAddress);
            sb.append('\t');
            sb.append(String.format("0x%08X", eventHandlerListAddress));
            sb.append('\t');
            sb.append(eventHandlerListCount);
            sb.append('\t');

            if (redirectorAddress > -1) {

                List<String> eventHandlers = new ArrayList<>(eventHandlerListCount);
                for (int j = 0; j < eventHandlerListCount; ++j) {
                    int eventHandler = battleOvl.readWord(eventHandlerListAddress + j * 8);
                    eventHandlers.add(String.format("0x%02X", eventHandler));
                }

                sb.append(String.join(", ", eventHandlers));
            }

            strs.add(sb.toString());
            romHandler.log(sb.toString());
        }

        return;
    }

    private void setDebugMode() {
        int dsAddress = 0x0207AC88;
        int dsiAddress = 0x0207AC84;

        int ds = arm9.readWord(dsAddress);
        int dsi = arm9.readWord(dsiAddress);

        arm9.writeWord(dsAddress, ds | 0x02000000, false);
        arm9.writeWord(dsiAddress, dsi | 0x02000000, false);
    }

    // Disables battle random value (0.9x-1.0x damage value)
    // Helpful for testing things and getting consistent results
    public void disableRandomness() {
        // Always do 100% damage, no variance
        int battleRandAddress = battleOvl.find("64 21 08 1A 00 04 00 0C 78 43 64 21");
        battleOvl.writeHalfword(battleRandAddress + 2, 0x2064);

        // Remove crit chance at no change to ratio
//            int critChanceAddress = battleOvl.find(Gen5Constants.critChanceLocator);
//            if (critChanceAddress < 0)
//                critChanceAddress = battleOvl.find("1808020101");
//
//            battleOvl.writeByte(critChanceAddress, 0);

        // Remove bonus/drawback from natures
        int natureStatAdjustAddress = globalAddressMap.getRamAddress(arm9, "Data_StatNatureAdjust");
        int natureStatAddress = globalAddressMap.getRamAddress(arm9, "Data_NatureStat");
        for (int i = 0; i < 5 * 25; ++i) {
            arm9.writeByte(natureStatAdjustAddress + i, 0);
            arm9.writeByte(natureStatAddress + i, 0);
        }

        System.out.println("disabled randomness");
    }

    private void registerAbilityEffects() {
        System.out.println("registering abilities...");
        registerBattleObjects("Ability", getAbilityListAddress(), getAbilityListCount());
    }

    private void registerMoveEffects() {
        System.out.println("registering moves...");
        registerBattleObjects("Move", getMoveListAddress(), getMoveListCount());
    }

    private void registerItemEffects() {
        System.out.println("registering items...");
        registerBattleObjects("Item", getItemListAddress(), getItemListCount());
    }

    private void registerBattleObjects(String name, int listAddress, int listCount) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < listCount; ++i) {
            int objectNumberAddress = listAddress + i * 8;
            int sourceAddress = objectNumberAddress + 4;

            int objectNumber = battleOvl.readWord(objectNumberAddress);
            int redirectorAddress = battleOvl.readWord(sourceAddress) - 1;
            Utils.printProgress(listCount, i, String.format("%3d -> 0x%08X", objectNumber, redirectorAddress));

            if (objectNumber == 0)
                continue;

            int eventHandlerListCount = getEventHandlerListCountFromRedirector(redirectorAddress);
            int listReferenceAddress = getRedirectorListReferenceAddress(redirectorAddress);
            int eventHandlerListAddress = battleOvl.readWord(listReferenceAddress);

            for (int j = 0; j < eventHandlerListCount; ++j) {
                int eventHandlerReferenceAddress = eventHandlerListAddress + j * 8 + 4;
                int eventHandlerAddress = battleOvl.readWord(eventHandlerReferenceAddress) - 1;

                String eventHandlerLabel = String.format("%sEventHandler_0x%08X", name, eventHandlerAddress);
                if (!globalAddressMap.isValidLabel(battleOvl, eventHandlerLabel))
                    // Register event handler
                    globalAddressMap.registerCodeAddress(battleOvl, eventHandlerLabel, eventHandlerAddress, 2);
            }

            // Register event handler list
            String eventHandlerListLabel = String.format("%sEventHandlerList_0x%08X", name, eventHandlerListAddress);
            if (!globalAddressMap.isValidLabel(battleOvl, eventHandlerListLabel))
                globalAddressMap.registerDataAddress(battleOvl, eventHandlerListLabel, eventHandlerListAddress, eventHandlerListCount * 8, "4*");

            // Register redirector
            String redirectorLabel = String.format("%sRedirector_0x%08X", name, redirectorAddress);
            if (!globalAddressMap.isValidLabel(battleOvl, redirectorLabel))
                globalAddressMap.registerCodeAddress(battleOvl, redirectorLabel, redirectorAddress, 2);

            // Set reference from list to redirector
            globalAddressMap.addReference(battleOvl, redirectorAddress, battleOvl, sourceAddress);
        }
        Utils.printProgressFinished(startTime, listCount);
        System.out.println();
    }

    private void relocateAbilityListRamAddress(int additionalCount) {
        int oldAddress = getAbilityListAddress();

        int cmpInstructionAddress = globalAddressMap.getRamAddress(battleOvl, "Inst_AbilityEffectListCountCmp");
        int oldCount = battleOvl.readUnsignedByte(cmpInstructionAddress);
        int newCount = oldCount + additionalCount;
        battleOvl.writeByte(cmpInstructionAddress, newCount);

        int effectListAddressDataAddress = globalAddressMap.getRamAddress(battleOvl, "Data_AbilityEffectListAddress");
        int effectListAddress4DataAddress = globalAddressMap.getRamAddress(battleOvl, "Data_AbilityEffectListAddress4");

        System.out.println("relocating abilities...");
        relocateObjectList("Data_AbilityEffectList", oldAddress, oldCount, newCount, effectListAddressDataAddress, effectListAddress4DataAddress);
    }

    private void relocateMoveListRamAddress(int additionalCount) {
        int oldAddress = getMoveListAddress();

        int countAddress = globalAddressMap.getRamAddress(battleOvl, "Data_MoveEffectListCount");
        int oldCount = battleOvl.readWord(countAddress);
        int newCount = oldCount + additionalCount;
        battleOvl.writeWord(countAddress, newCount, false);

        int effectListAddressDataAddress = globalAddressMap.getRamAddress(battleOvl, "Data_MoveEffectListAddress");
        int effectListAddress4DataAddress = globalAddressMap.getRamAddress(battleOvl, "Data_MoveEffectListAddress4");

        System.out.println("relocating moves...");
        relocateObjectList("Data_MoveEffectList", oldAddress, oldCount, newCount, effectListAddressDataAddress, effectListAddress4DataAddress);
    }

    private void relocateItemListRamAddress(int additionalCount) {
        int oldAddress = getItemListAddress();

        int cmpInstructionAddress = globalAddressMap.getRamAddress(battleOvl, "Inst_ItemEffectListCountCmp");
        int oldCount = battleOvl.readUnsignedByte(cmpInstructionAddress);
        int newCount = oldCount + additionalCount;
        battleOvl.writeByte(cmpInstructionAddress, newCount);

        int effectListAddressDataAddress = globalAddressMap.getRamAddress(battleOvl, "Data_ItemEffectListAddress");
        int effectListAddress4DataAddress = globalAddressMap.getRamAddress(battleOvl, "Data_ItemEffectListAddress4");

        System.out.println("relocating items...");
        relocateObjectList("Data_ItemEffectList", oldAddress, oldCount, newCount, effectListAddressDataAddress, effectListAddress4DataAddress);
    }

    private void relocateObjectList(String label, int oldAddress, int oldCount, int newCount, int effectListAddressDataAddress,
                                    int effectListAddress4DataAddress) {
        int newSize = newCount * 8;
        byte[] data = new byte[newSize];

        int writeIndex = 0;
        int readIndex = 0;
        while (readIndex < oldCount) {
            int number = battleOvl.readWord(oldAddress + readIndex * 8);
            int redirectorAddress = battleOvl.readWord(oldAddress + readIndex * 8 + 4);

            if (number == 0) {
                if (redirectorAddress != 0)
                    throw new RuntimeException("If the number was 0, we should expect the redirector address to also be 0");

                ++readIndex;
                continue;
            }

            writeWord(data, writeIndex * 8, number);
            writeWord(data, writeIndex * 8 + 4, redirectorAddress);

            ++writeIndex;
            ++readIndex;
        }

        // Fill rest with 0x00
        for (int i = writeIndex * 8; i < data.length; ++i) {
            data[i] = 0x00;
        }

        int newAddress = battleOvl.newData(data, label, "4*");
        battleOvl.writeWord(effectListAddressDataAddress, newAddress, false);
        battleOvl.writeWord(effectListAddress4DataAddress, newAddress + 4, false);

    }

    private int getAbilityListAddress() {
        return globalAddressMap.getRamAddress(battleOvl, "Data_AbilityEffectList");
    }

    private int getMoveListAddress() {
        return globalAddressMap.getRamAddress(battleOvl, "Data_MoveEffectList");
    }

    private int getItemListAddress() {
        return globalAddressMap.getRamAddress(battleOvl, "Data_ItemEffectList");
    }

    private int getRedirectorCountSetAddress(int funcAddress) {
        int returnValue = -1;

        int funcSize = battleOvl.getFuncSizeRom(funcAddress);
        for (int i = 0; i < funcSize; i += 2) {
            int instruction = battleOvl.readUnsignedHalfword(funcAddress + i);

            // MOV R1
            if ((instruction & 0xFF00) == 0x2100) {
                returnValue = funcAddress + i;
                continue;
            }

            // BX LR
            if ((instruction & 0xFF00) == 0x4700)
                break;

            // POP
            if ((instruction & 0xFE00) == 0xBC00)
                break;
        }

        return returnValue;
    }

    private int getRedirectorListReferenceAddress(int funcAddress) {
        int returnValue = -1;

        int funcSize = battleOvl.getFuncSizeRom(funcAddress);
        for (int i = 0; i < funcSize; i += 2) {
            int instruction = battleOvl.readUnsignedHalfword(funcAddress + i);

            // LDR R0
            if ((instruction & 0xFF00) == 0x4800) {
                int eventListBranchOffset = i + ((instruction & 0x00FF) << 2) + 4;
                returnValue = alignWord(funcAddress + eventListBranchOffset);
                continue;
            }

            // BX LR
            if ((instruction & 0xFF00) == 0x4700)
                break;

            // POP
            if ((instruction & 0xFE00) == 0xBC00)
                break;
        }

        return returnValue;
    }

    private int getEventHandlerListCountFromRedirector(int redirectorAddress) {
        int countSetAddress = getRedirectorCountSetAddress(redirectorAddress);
        if (countSetAddress <= 0)
            throw new RuntimeException();

        return battleOvl.readUnsignedByte(countSetAddress);
    }

    private int getEventHandlerListAddressFromRedirector(int redirectorAddress) {
        int eventListReferenceAddress = getRedirectorListReferenceAddress(redirectorAddress);
        if (eventListReferenceAddress <= 0)
            throw new RuntimeException();

        return battleOvl.readWord(eventListReferenceAddress);
    }

    private int getRedirectorAddress(int objectNumber, int objectListAddress, int objectListCount) {
        for (int i = 0; i < objectListCount; ++i) {
            if (objectNumber == battleOvl.readWord(objectListAddress + 8 * i)) {
                return battleOvl.readWord(objectListAddress + 8 * i + 4) - 1;
            }
        }

        throw new RuntimeException(String.format("Could not find object of index %d in list", objectNumber));
    }

    private int getMoveRedirectorAddress(int moveNumber) {
        int listAddress = getMoveListAddress();
        int listCount = getMoveListCount();
        return getRedirectorAddress(moveNumber, listAddress, listCount);
    }

    private int getAbilityRedirectorAddress(int abilityNumber) {
        int listAddress = getAbilityListAddress();
        int listCount = getAbilityListCount();
        return getRedirectorAddress(abilityNumber, listAddress, listCount);
    }

    private int getEventHandlerFuncReferenceAddress(int objectNumber, int objectListAddress, int objectListCount, int eventType) {
        int redirectorAddress = getRedirectorAddress(objectNumber, objectListAddress, objectListCount);
        int eventListCount = getEventHandlerListCountFromRedirector(redirectorAddress);
        int eventListAddress = getEventHandlerListAddressFromRedirector(redirectorAddress);

        for (int i = 0; i < eventListCount; ++i) {
            if (eventType == battleOvl.readWord(eventListAddress + 8 * i)) {
                return eventListAddress + 8 * i + 4;
            }
        }

        throw new RuntimeException(String.format("Could not find event handler on object %d for event type 0x%02X", objectNumber, eventType));
    }

    private abstract class BattleEventHandler {
        int type;
        int address = -1;

        protected void setFromFuncName(int type, String fullFuncName) {
            this.type = type;

            if (fullFuncName.contains("::")) {
                address = getFuncAddress(fullFuncName);
                return;
            }

            if (!globalAddressMap.isValidLabel(battleOvl, fullFuncName)) {
                List<String> lines = readLines(String.format("eventhandlers/%s/%s.s", getFuncDirectory(), fullFuncName));

                battleOvl.writeCode(lines, fullFuncName);
            }

            address = globalAddressMap.getRamAddress(battleOvl, fullFuncName);
        }

        protected abstract String getFuncDirectory();
    }

    private class AbilityEventHandler extends BattleEventHandler {
        AbilityEventHandler(int type) {
            this.type = type;
        }

        AbilityEventHandler(int type, String funcName) {
            setFromFuncName(type, funcName);
        }

        @Override
        protected String getFuncDirectory() {
            return "ability";
        }
    }

    private class MoveEventHandler extends BattleEventHandler {
        MoveEventHandler(int type) {
            this.type = type;
        }

        MoveEventHandler(int type, String funcName) {
            setFromFuncName(type, funcName);
        }

        @Override
        protected String getFuncDirectory() {
            return "move";
        }
    }

    private class ItemEventHandler extends BattleEventHandler {
        ItemEventHandler(int type, String funcName) {
            setFromFuncName(type, funcName);
        }

        ItemEventHandler(int type, int existingItem) {
            this.type = type;

            int referenceAddress = getEventHandlerFuncReferenceAddress(existingItem, getItemListAddress(), getItemListCount(), type);
            this.address = battleOvl.readWord(referenceAddress) - 1;
        }

        @Override
        protected String getFuncDirectory() {
            return "item";
        }
    }

    private void setBattleObject(int number, int index, int objectListAddress, BattleEventHandler... eventHandlers) {
        int eventHandlerListSize = eventHandlers.length * 8;

        for (BattleEventHandler eventHandler : eventHandlers) {
            if (eventHandler.address > 0)
                continue;

            int redirectorAddress = battleOvl.readWord(objectListAddress + index * 8 + 4) - 1;
            int eventHandlerListAddress = getEventHandlerListAddressFromRedirector(redirectorAddress);
            int eventHandlerListCount = getEventHandlerListCountFromRedirector(redirectorAddress);

            for (int i = 0; i < eventHandlerListCount; ++i) {
                int eventHandlerType = battleOvl.readWord(eventHandlerListAddress + i * 8);
                if (eventHandlerType == eventHandler.type) {
                    eventHandler.address = battleOvl.readWord(eventHandlerListAddress + i * 8 + 4) - 1;
                    break;
                }
            }

            if (eventHandler.address <= 0)
                throw new RuntimeException(String.format("Object %d did not have an event handler for 0x%02X", number, eventHandler.type));
        }

        // Write event handler list
        byte[] eventHandlerListData = new byte[eventHandlerListSize];
        for (int i = 0; i < eventHandlers.length; ++i) {
            BattleEventHandler eventHandler = eventHandlers[i];
            writeWord(eventHandlerListData, i * 8, eventHandler.type);
            writeWord(eventHandlerListData, i * 8 + 4, eventHandler.address + 1);
        }
        int eventHandlerListAddress = battleOvl.writeDataUnnamed(eventHandlerListData, "4*");

        // Write redirector function
        List<String> lines = Arrays.asList(
                "mov r1, #" + eventHandlers.length,
                "str r1, [r0]",
                "ldr r0, =" + eventHandlerListAddress,
                "bx lr"
        );
        int redirectorFuncAddress = battleOvl.writeCodeUnnamed(lines);

        // Write to object list
        battleOvl.writeWord(objectListAddress + index * 8, number, false);
        battleOvl.writeWord(objectListAddress + index * 8 + 4, redirectorFuncAddress + 1, true);
    }

    private void setAbilityEventHandlers(int abilityNumber, AbilityEventHandler... events) {
        int abilityListRamAddress = getAbilityListAddress();
        int abilityListCount = getAbilityListCount();
        int index = getBattleObjectIndex(abilityListRamAddress, abilityListCount, abilityNumber);
        setBattleObject(abilityNumber, index, abilityListRamAddress, events);
    }

    private void setMoveEventHandlers(int moveNumber, MoveEventHandler... events) {
        int moveListAddress = getMoveListAddress();
        int moveListCount = getMoveListCount();
        int index = getBattleObjectIndex(moveListAddress, moveListCount, moveNumber);
        setBattleObject(moveNumber, index, moveListAddress, events);
    }

    private void cloneMoveEventHandlers(int moveNumber, int otherMoveNumber) {
        int moveListAddress = getMoveListAddress();
        int listIndex = getBattleObjectIndex(moveListAddress, getMoveListCount(), moveNumber);
        int moveRedirectorAddress = getMoveRedirectorAddress(otherMoveNumber);
        battleOvl.writeWord(moveListAddress + listIndex * 8, moveNumber, false);
        battleOvl.writeWord(moveListAddress + listIndex * 8 + 4, moveRedirectorAddress + 1, true);
    }

    private void setItemEventHandlers(int itemNumber, ItemEventHandler... events) {
        int itemListAddress = getItemListAddress();
        int itemListCount = getItemListCount();
        int index = getBattleObjectIndex(itemListAddress, itemListCount, itemNumber);
        setBattleObject(itemNumber, index, itemListAddress, events);
    }

    private void cloneItemEventHandlersFromAbility(int itemNumber, int abilityNumber) {
        int itemListAddress = getItemListAddress();
        int listIndex = getBattleObjectIndex(itemListAddress, getItemListCount(), itemNumber);
        int abilityRedirectorAddress = getAbilityRedirectorAddress(abilityNumber);
        battleOvl.writeWord(itemListAddress + listIndex * 8, itemNumber, false);
        battleOvl.writeWord(itemListAddress + listIndex * 8 + 4, abilityRedirectorAddress + 1, true);
    }

    private void clearMoveEventHandlers(int moveNumber) {
        int moveListAddress = getMoveListAddress();
        int moveListCount = getMoveListCount();
        int index = getBattleObjectIndex(moveListAddress, moveListCount, moveNumber);

        battleOvl.writeWord(moveListAddress + index * 8, 0, false);
        battleOvl.writeWord(moveListAddress + index * 8 + 4, 0, true);
    }

    private int getAbilityListCount() {
        int cmpInstructionAddress = globalAddressMap.getRamAddress(battleOvl, "Inst_AbilityEffectListCountCmp");
        return battleOvl.readUnsignedByte(cmpInstructionAddress);
    }

    private int getMoveListCount() {
        int dataAddress = globalAddressMap.getRamAddress(battleOvl, "Data_MoveEffectListCount");
        return battleOvl.readWord(dataAddress);
    }

    private int getItemListCount() {
        int cmpInstructionAddress = globalAddressMap.getRamAddress(battleOvl, "Inst_ItemEffectListCountCmp");
        return battleOvl.readUnsignedByte(cmpInstructionAddress);
    }

    private int getBattleObjectIndex(int listRamAddress, int listCount, int objectNumber) {
        // Find exact match
        for (int i = 0; i < listCount; ++i) {
            int address = listRamAddress + i * 8;
            int thisObjectNumber = battleOvl.readWord(address);
            if (objectNumber == thisObjectNumber) {
                return i;
            }
        }

        // Find first empty
        for (int i = 0; i < listCount; ++i) {
            int address = listRamAddress + i * 8;
            int thisObjectNumber = battleOvl.readWord(address);
            if (thisObjectNumber == 0) {
                return i;
            }
        }

        return -1;
    }

    public int getFuncAddress(String fullFuncName) {
        if (!fullFuncName.contains("::"))
            throw new RuntimeException(String.format("Could not find function \"%s\".", fullFuncName));

        String[] strs = fullFuncName.split("::", 2);
        return globalAddressMap.getRamAddress(strs[0], strs[1]);
    }

    public void logUpdates(String directory) {
        // Abilities
        ParagonLiteDocWriter docWriter = new ParagonLiteDocWriter();
        docWriter.h1("Ability Changes");

        ParagonLiteDocWriter.Table abilityNameChangesTable = new ParagonLiteDocWriter.Table("Notes");
        for (int i = 0; i <= Abilities.teravolt; ++i) {
            String name = abilityNames.get(i);
            String oldName = oldAbilityNames.get(i);
            boolean updatedName = false;
            if (!Objects.equals(name, oldName)) {
                name = String.format("#%03d %s >> %s%n", i, oldName, name);
                updatedName = true;
            }

            boolean hasUpdateNote = abilityUpdates.containsKey(i);
            if (updatedName || hasUpdateNote) {
                String note = hasUpdateNote ? abilityUpdates.get(i) : "";
                abilityNameChangesTable.addRow(name, note);
            }
        }
        docWriter.write(directory + File.separator + "AbilityChanges");

        // Pokemon
        Map<Pokemon, List<Pokemon>> formes = new HashMap<>();
        for (Pokemon poke : pokes) {
            if (poke == null)
                continue;

            if (poke.baseForme == null || poke.baseForme == poke)
                continue;

            if (!formes.containsKey(poke.baseForme)) {
                List<Pokemon> formeList = new ArrayList<>();
                formeList.add(poke.baseForme);
                formes.put(poke.baseForme, formeList);
            }
            formes.get(poke.baseForme).add(poke);
        }

        docWriter = new ParagonLiteDocWriter();
        docWriter.h1("Pokémon Changes");
        for (int i = 1; i < pokes.length; ++i) {
            List<Pokemon> pokeFormes = formes.getOrDefault(pokes[i], Collections.singletonList(pokes[i]));
            for (Pokemon poke : pokeFormes) {
                PokeUpdate pokeUpdate = pokeUpdates[poke.number];
                if (!pokeUpdate.hasAnyUpdate(oldAbilityNames, abilityNames))
                    continue;

                docWriter.horizontalRule();
                docWriter.h2(String.format("#%03d %s", pokes[i].number, poke.fullName()));

                // Type
                if (pokeUpdate.hasTypeUpdate()) {
                    Type oldType1 = Type.values()[poke.primaryType.ordinal() - pokeUpdate.type1];

                    int oldType2Ordinal = (poke.secondaryType == null ? -1 : poke.secondaryType.ordinal()) - pokeUpdate.type2;
                    Type oldType2 = oldType2Ordinal < 0 ? null : Type.values()[oldType2Ordinal];
                    String oldTypeStr = oldType2 == null ? oldType1.camelCase() : String.format("%s/%s", oldType1.camelCase(), oldType2.camelCase());

                    docWriter.paragraph("Old Type: " + oldTypeStr, "New Type: " + poke.getTypeString(true));
                } else {
                    docWriter.paragraph("Type: " + poke.getTypeString(false));
                }

                // Ability
                ParagonLiteDocWriter.Table abilityTable;
                if (pokeUpdate.hasAbilityUpdate(oldAbilityNames, abilityNames)) {
                    abilityTable = new ParagonLiteDocWriter.Table("Old Ability", "New Ability");
                    String oldAbility1 = oldAbilityNames.get(poke.ability1 - pokeUpdate.ability1);
                    String oldAbility2 = oldAbilityNames.get(poke.ability2 - pokeUpdate.ability2);
                    String oldAbility3 = oldAbilityNames.get(poke.ability3 - pokeUpdate.ability3);

                    abilityTable.addRow("1", oldAbility1, abilityNames.get(poke.ability1));
                    abilityTable.addRow("2", oldAbility2, abilityNames.get(poke.ability2));
                    abilityTable.addRow("Hidden", oldAbility3, abilityNames.get(poke.ability3));
                } else {
                    abilityTable = new ParagonLiteDocWriter.Table("Ability");
                    abilityTable.addRow("1", abilityNames.get(poke.ability1));
                    abilityTable.addRow("2", abilityNames.get(poke.ability2));
                    abilityTable.addRow("Hidden", abilityNames.get(poke.ability3));
                }
                docWriter.table(abilityTable);

                // Exp. Yield
                if (pokeUpdate.hasExpYieldUpdate()) {
                    docWriter.paragraph("Old Exp. Yield: " + (poke.expYield - pokeUpdate.expYield), String.format("New Exp. Yield: %s (%+d)", poke.expYield, pokeUpdate.expYield));
                } else {
                    docWriter.paragraph("Exp. Yield: " + poke.expYield);
                }

                // Stats
                if (pokeUpdate.hasStatsUpdate()) {
                    docWriter.h3("Old Stats");
                    ParagonLiteDocWriter.Table oldStatsTable = new ParagonLiteDocWriter.Table("Value", "");
                    oldStatsTable.addRow("HP", String.valueOf(poke.hp - pokeUpdate.hp), getStatBar(poke.hp - pokeUpdate.hp));
                    oldStatsTable.addRow("Attack", String.valueOf(poke.attack - pokeUpdate.attack), getStatBar(poke.attack - pokeUpdate.attack));
                    oldStatsTable.addRow("Defense", String.valueOf(poke.defense - pokeUpdate.defense), getStatBar(poke.defense - pokeUpdate.defense));
                    oldStatsTable.addRow("Sp. Atk", String.valueOf(poke.spatk - pokeUpdate.spatk), getStatBar(poke.spatk - pokeUpdate.spatk));
                    oldStatsTable.addRow("Sp. Def", String.valueOf(poke.spdef - pokeUpdate.spdef), getStatBar(poke.spdef - pokeUpdate.spdef));
                    oldStatsTable.addRow("Speed", String.valueOf(poke.speed - pokeUpdate.speed), getStatBar(poke.speed - pokeUpdate.speed));
                    oldStatsTable.addRow("BST", String.valueOf(poke.bst() - pokeUpdate.bst()), "");
                    docWriter.table(oldStatsTable);

                    docWriter.h3("New Stats");
                    ParagonLiteDocWriter.Table newStatsTable = new ParagonLiteDocWriter.Table("Value", "", "Change");
                    newStatsTable.addRow("HP", String.valueOf(poke.hp), getStatBar(poke.hp), String.format("%+d", pokeUpdate.hp));
                    newStatsTable.addRow("Attack", String.valueOf(poke.attack), getStatBar(poke.attack), String.format("%+d", pokeUpdate.attack));
                    newStatsTable.addRow("Defense", String.valueOf(poke.defense), getStatBar(poke.defense), String.format("%+d", pokeUpdate.defense));
                    newStatsTable.addRow("Sp. Atk", String.valueOf(poke.spatk), getStatBar(poke.spatk), String.format("%+d", pokeUpdate.spatk));
                    newStatsTable.addRow("Sp. Def", String.valueOf(poke.spdef), getStatBar(poke.spdef), String.format("%+d", pokeUpdate.spdef));
                    newStatsTable.addRow("Speed", String.valueOf(poke.speed), getStatBar(poke.speed), String.format("%+d", pokeUpdate.speed));
                    newStatsTable.addRow("BST", String.valueOf(poke.bst()), "", String.format("%+d", pokeUpdate.bst()));
                    docWriter.table(newStatsTable);
                } else {
                    docWriter.h3("Stats");
                    ParagonLiteDocWriter.Table statsTable = new ParagonLiteDocWriter.Table("Value", "");
                    statsTable.addRow("HP", String.valueOf(poke.hp), getStatBar(poke.hp));
                    statsTable.addRow("Attack", String.valueOf(poke.attack), getStatBar(poke.attack));
                    statsTable.addRow("Defense", String.valueOf(poke.defense), getStatBar(poke.defense));
                    statsTable.addRow("Sp. Atk", String.valueOf(poke.spatk), getStatBar(poke.spatk));
                    statsTable.addRow("Sp. Def", String.valueOf(poke.spdef), getStatBar(poke.spdef));
                    statsTable.addRow("Speed", String.valueOf(poke.speed), getStatBar(poke.speed));
                    statsTable.addRow("BST", String.valueOf(poke.bst()), "");
                    docWriter.table(statsTable);
                }
            }
        }
        docWriter.write(directory + File.separator + "PokémonChanges");
    }

    private static String getStatBar(int value) {
        String bar = "█".repeat(value / 10) + (value % 10 >= 5 ? "▌" : "");
        return String.format("`%s%s`", bar, ".".repeat(26 - bar.length()));
    }

    private static int alignWord(int address) {
        return address & 0xFFFFFFFC;
    }

    private static List<String> readLines(String filename) {
        Scanner sc;
        try {
            InputStream stream = FileFunctions.openConfig("paragonlite/" + filename);
            sc = new Scanner(stream, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<String> lines = new ArrayList<>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }

        return lines;
    }

    private void setItemSprite(int number, String sprite) {
        setItemSpriteInternal(number, -1, sprite);
    }

    private void setItemSprite(int number, int baseItem, String paletteSprite) {
        setItemSpriteInternal(number, baseItem, paletteSprite);
    }

    private void setItemSpriteInternal(int number, int baseItemNumber, String sprite) {
        String extension = ".bmp";
        if (!sprite.endsWith(extension))
            sprite += extension;

        byte[] bytes;
        try {
            InputStream stream = FileFunctions.openConfig(String.format("paragonlite/itemsprites/%s", sprite));
            bytes = new byte[stream.available()];
            if (stream.read(bytes) == -1)
                throw new IOException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BitmapFile bitmapFile;
        try {
            bitmapFile = new BitmapFile(bytes);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }

        int itemSpriteMapAddress = globalAddressMap.getRamAddress(arm9, "Data_ItemSpriteMap");

        int itemGraphicAddress = itemSpriteMapAddress + number * 4;
        int itemGraphicId;
        if (baseItemNumber < 0) {
            itemGraphicId = itemGraphicsNarc.files.size();
            byte[] graphicFile = bitmapFile.writeGraphicFile32x32();
            itemGraphicsNarc.files.add(graphicFile);
        } else {
            itemGraphicId = arm9.readUnsignedHalfword(itemSpriteMapAddress + baseItemNumber * 4);
        }
        arm9.writeHalfword(itemGraphicAddress, itemGraphicId);

        int itemPaletteAddress = itemSpriteMapAddress + number * 4 + 2;
        int itemPaletteId = itemGraphicsNarc.files.size();
        byte[] paletteFile = bitmapFile.writePaletteFile();
        itemGraphicsNarc.files.add(paletteFile);
        arm9.writeHalfword(itemPaletteAddress, itemPaletteId);
    }

    private void writeTrainerAIFile(NARCArchive narc, int number) {
        byte[] bytes;
        try {
            InputStream stream = FileFunctions.openConfig(String.format("paragonlite/traineraiscripts/%d.bin", number));
            bytes = new byte[stream.available()];
            if (stream.read(bytes) == -1)
                throw new IOException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (number >= narc.files.size()) {
            narc.files.add(new byte[0]);
        }

        narc.files.set(number, bytes);
    }

    private static void writeHalf(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private static void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }
}
