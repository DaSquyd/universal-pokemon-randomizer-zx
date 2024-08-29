package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.BitmapFile;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.GFXFunctions;
import com.dabomstew.pkrandom.Utils;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.DataFormatException;

public class ParagonLiteHandler {

    public enum Mode {
        ParagonLite, Redux
    }

    public static Mode mode = Mode.ParagonLite;

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
                    || !oldAbilityNames.get(ability1).equals(newAbilityNames.get(ability1)) || !oldAbilityNames.get(ability2).equals(newAbilityNames.get(ability2)) || !oldAbilityNames.get(ability3).equals(newAbilityNames.get(ability3));
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

    ParagonLiteHandler(Gen5RomHandler romHandler, byte[] arm9Data, int battleOvlNumber, int battleServerOvlNumber, int trainerAIOvlNumber, Pokemon[] pokes, Move[] moves, NARCArchive pokemonGraphicsNarc, NARCArchive itemDataNarc, NARCArchive itemGraphicsNarc, List<String> battleEventStrings1, List<String> battleEventStrings2, List<String> abilityNames, List<String> abilityDescriptions, List<String> abilityExplanations, List<String> moveNames, List<String> moveDescriptions, List<String> itemNames, List<String> itemNameMessages, List<String> itemPluralNames, List<String> itemDescriptions) {
        this.romHandler = romHandler;

        globalAddressMap = new ParagonLiteAddressMap();
        try {
            arm9 = new ParagonLiteArm9(romHandler, arm9Data, globalAddressMap);

            byte[] battleOvlData = romHandler.readOverlay(battleOvlNumber);
            int battleOvlAddress = romHandler.getOverlayAddress(battleOvlNumber);
            battleOvl = new ParagonLiteOverlay(romHandler, battleOvlNumber, "Battle", battleOvlData, battleOvlAddress, ParagonLiteOverlay.Insertion.Front, globalAddressMap);

            byte[] battleServerOvlData = romHandler.readOverlay(battleServerOvlNumber);
            int battleServerOvlAddress = romHandler.getOverlayAddress(battleServerOvlNumber);
            battleServerOvl = new ParagonLiteOverlay(romHandler, battleServerOvlNumber, "BattleServer", battleServerOvlData, battleServerOvlAddress, ParagonLiteOverlay.Insertion.Front, globalAddressMap);

            byte[] trainerAIOvlData = romHandler.readOverlay(trainerAIOvlNumber);
            int trainerAIOvlAddress = romHandler.getOverlayAddress(trainerAIOvlNumber);
            trainerAIOvl = new ParagonLiteOverlay(romHandler, trainerAIOvlNumber, "TrainerAI", trainerAIOvlData, trainerAIOvlAddress, ParagonLiteOverlay.Insertion.Back, globalAddressMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int gameIndex = romHandler.getGen5GameIndex();
        if (gameIndex < 0 || gameIndex > 3) throw new RuntimeException();

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
            if (addressStartColumn + gameIndex >= values.length) throw new RuntimeException();

            String offsetStr = values[addressStartColumn + gameIndex];
            if (offsetStr.equals("--")) continue;

            if (!offsetStr.startsWith("0x")) throw new RuntimeException();

            offsetStr = offsetStr.substring(2); // remove 0x
            int romAddress = Integer.parseUnsignedInt(offsetStr, 16);

            int dataLen;
            switch (values[typeColumn].toLowerCase()) {
                case "code":
                    int encoding;
                    switch (values[encodingColumn]) {
                        case "arm" -> encoding = 4;
                        case "thumb" -> encoding = 2;
                        case "arm,thumb" -> encoding = gameIndex == 0 || gameIndex == 1 ? 4 : 2;
                        case "thumb,arm" -> encoding = gameIndex == 0 || gameIndex == 1 ? 2 : 4;
                        default -> throw new IllegalStateException(String.format("Unexpected encoding for %s::%s: %s", namespace, label, values[encodingColumn]));
                    }
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
                    throw new RuntimeException(String.format("Unknown type for %s::%s: %s", namespace, label, values[typeColumn]));
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
        while (battleEventStrings1.size() < 221) battleEventStrings1.add("");

        // Assault Vest
        battleEventStrings1.add /* 221 */("The effects of the Assault Vest prevent\\xFFFEstatus moves from being used!");
        battleEventStrings1.add /* 222 */("\uF000Č\\x0001\\x0000 can't use status moves!");
    }

    private void setBattleEventStrings2() {
        // Heal Prevention (Heal Block + Psychic Noise
        battleEventStrings2.set(884, "\uF000Ă\\x0001\\x0000's healing prevention\\xFFFEwore off!");
        battleEventStrings2.set(885, "The wild \uF000Ă\\x0001\\x0000's healing prevention\\xFFFEwore off!");
        battleEventStrings2.set(886, "The foe's \uF000Ă\\x0001\\x0000's healing prevention\\xFFFEwore off!");
        battleEventStrings2.set(887, "\uF000Ă\\x0001\\x0000 was prevented\\xFFFEfrom healing!");
        battleEventStrings2.set(888, "The wild \uF000Ă\\x0001\\x0000 was prevented\\xFFFEfrom healing!");
        battleEventStrings2.set(889, "The foe's \uF000Ă\\x0001\\x0000 was prevented\\xFFFEfrom healing!");
        battleEventStrings2.set(890, "\uF000Ă\\x0001\\x0000 can't use\\xFFFE\uF000ć\\x0001\\x0001 because healing is prevented!");
        battleEventStrings2.set(891, "The wild \uF000Ă\\x0001\\x0000 can't use\\xFFFE\uF000ć\\x0001\\x0001 because healing is prevented!");
        battleEventStrings2.set(892, "The foe's \uF000Ă\\x0001\\x0000 can't use\\xFFFE\uF000ć\\x0001\\x0001 because healing is prevented!");

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
        switch (mode) {
            case ParagonLite -> {
                battleEventStrings2.add/* 1180 */("\uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");
                battleEventStrings2.add/* 1181 */("The wild \uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");
                battleEventStrings2.add/* 1182 */("The foe's \uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");
            }
            case Redux -> {
                battleEventStrings2.add/* 1180 */("\uF000Ă\\x0001\\x0000 stalks\\xFFFEthe shadows!");
                battleEventStrings2.add/* 1181 */("The wild \uF000Ă\\x0001\\x0000 stalks\\xFFFEthe shadows!");
                battleEventStrings2.add/* 1182 */("The foe's \uF000Ă\\x0001\\x0000 stalks\\xFFFEthe shadows!");
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

        // Arena Trap
        switch (mode) {
            case ParagonLite -> {
                battleEventStrings2.add/* 1183 */("\uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");
                battleEventStrings2.add/* 1184 */("The wild \uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");
                battleEventStrings2.add/* 1185 */("The foe's \uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");
            }
            case Redux -> {
                battleEventStrings2.add/* 1183 */("\uF000Ă\\x0001\\x0000 traps\\xFFFEthe arena!");
                battleEventStrings2.add/* 1184 */("The wild \uF000Ă\\x0001\\x0000 traps\\xFFFEthe arena!");
                battleEventStrings2.add/* 1185 */("The foe's \uF000Ă\\x0001\\x0000 traps\\xFFFEthe arena!");
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

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
        battleEventStrings2.add/* 1194 */("\uF000Ă\\x0001\\x0000 protects its allies with\\xFFFEits stone shell!");
        battleEventStrings2.add/* 1195 */("The wild \uF000Ă\\x0001\\x0000 protects its allies with\\xFFFEits stone shell!");
        battleEventStrings2.add/* 1196 */("The foe's \uF000Ă\\x0001\\x0000 protects its allies with\\xFFFEits stone shell!");

        // Water Veil
        battleEventStrings2.add/* 1197*/("\uF000Ă\\x0001\\x0000 is veiled\\xFFFEin water!");
        battleEventStrings2.add/* 1198*/("The wild \uF000Ă\\x0001\\x0000 is veiled\\xFFFEin water!");
        battleEventStrings2.add/* 1199*/("The foe's \uF000Ă\\x0001\\x0000 is veiled\\xFFFEin water!");

        // Wind Power
        battleEventStrings2.add/* 1200*/("Being hit by \uF000ć\\x0001\\x0000 charged\\xFFFE\uF000Ă\\x0001\\x0001 with power!");
        battleEventStrings2.add/* 1201*/("Being hit by \uF000ć\\x0001\\x0000 charged\\xFFFEthe wild \uF000Ă\\x0001\\x0001 with power!");
        battleEventStrings2.add/* 1202*/("Being hit by \uF000ć\\x0001\\x0000 charged\\xFFFEthe foe's \uF000Ă\\x0001\\x0001 with power!");

        // Supreme Overloard
        battleEventStrings2.add/* 1203*/("\uF000Ă\\x0001\\x0000 gained strength\\xFFFEfrom the fallen!");
        battleEventStrings2.add/* 1204*/("The wild \uF000Ă\\x0001\\x0000 gained strength\\xFFFEfrom the fallen!");
        battleEventStrings2.add/* 1205*/("The foe's \uF000Ă\\x0001\\x0000 gained strength\\xFFFEfrom the fallen!");
    }

    public void setReadPokePersonalData() {
        // Updates the personal data to allow for abilities up to index 1023
        List<String> lines = readLines("read_poke_personal_data.s");
        arm9.writeCodeForceInline(lines, "ReadPokePersonalData");
        System.out.println("Set ReadPokePersonalData");
    }

    public void setReadPokeBoxData() {
        // Updates the box data to allow for abilities up to index 1023
        // Also fixes the Azurill->Marill gender bug
        List<String> lines = readLines("read_poke_box_data.s");
        arm9.writeCodeForceInline(lines, "ReadPokeBoxData");
        System.out.println("Set ReadPokeBoxData");
    }

    public void fixChallengeModeLevelBug() {
        if (!romHandler.isBlack2() && !romHandler.isWhite2()) return;

        // Challenge Mode and Normal mode are broken in B2W2.
        // After updating the levels for the Pokémon in an enemy trainer's team, the game doesn't recalculate the stats
        // the Pokémon should have.
        List<String> lines = readLines("difficulty_adjust_poke_level.s");
        arm9.writeCodeForceInline(lines, "Difficulty_AdjustPokeLevel");
        System.out.println("Fixed Challenge/Easy Mode level bug");
    }

    public void setDamageCalcOffensiveStat() {
        // Allows for stat to be modified (for Body Press)
        List<String> lines = readLines("damage_calc_get_offensive_stat.s");
        battleOvl.replaceCode(lines, "DamageCalc_GetOffensiveStat");
        System.out.println("Set damage calc defensive stat");
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
                GFXFunctions.makeARGBColor(104, 200, 152), // #68c898

                // Body Main
                GFXFunctions.makeARGBColor(224, 224, 232), // #e0e0e8
                GFXFunctions.makeARGBColor(176, 176, 184), // #b0b0b8
                GFXFunctions.makeARGBColor(128, 128, 136), // #808088
                GFXFunctions.makeARGBColor(88, 88, 88), // #585858

                // Body Highlight
                GFXFunctions.makeARGBColor(176, 112, 136), // #b07088
                GFXFunctions.makeARGBColor(152, 80, 112), // #985070
                GFXFunctions.makeARGBColor(96, 48, 72), // #603048

                // Ring
                GFXFunctions.makeARGBColor(248, 208, 216), // #f8d0d8
                GFXFunctions.makeARGBColor(240, 184, 196), // #f0b8c4
                GFXFunctions.makeARGBColor(184, 128, 152), // #b88098
                GFXFunctions.makeARGBColor(120, 88, 104), // #785868

                // Gems/Eye
                GFXFunctions.makeARGBColor(240, 128, 184), // #f080b8
                GFXFunctions.makeARGBColor(160, 56, 104), // #a03868

                // Iris
                GFXFunctions.makeARGBColor(152, 160, 168), // #98a0a8
        };
        int[] arceusFairyShiny = Arrays.copyOf(arceusFairyStandard, arceusFairyStandard.length);
        arceusFairyShiny[1] = GFXFunctions.makeARGBColor(248, 240, 136); // #f8f088
        arceusFairyShiny[2] = GFXFunctions.makeARGBColor(216, 192, 56); // #d8c038
        arceusFairyShiny[3] = GFXFunctions.makeARGBColor(168, 128, 64); // #a88040
        arceusFairyShiny[4] = GFXFunctions.makeARGBColor(104, 80, 32); // #685020

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

    public void setIsSelectedMoveValid() {
        // Implements logic for Assault Vest
        List<String> lines = readLines("is_selected_move_valid.s");
        battleOvl.replaceCode(lines, "IsSelectedMoveValid");

        System.out.println("Set IsSelectedMoveValid");
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

    public void setGhostEscape() {
        // Allow Ghost to always escape
        List<String> lines = readLines("is_poke_trapped.s");
        battleOvl.writeCodeForceInline(lines, "IsPokeTrapped");

        System.out.println("Set Ghost Escape");
    }

    public void setCallModifyEffectivenessHandler() {
        // Updates the call handler for modifying effectiveness.
        // This is originally used for Scrappy, Ring Target, Miracle Eye, Foresight, Odor Sleuth, Ingrain, and Grounded.
        // Now, it allows for modification of type effectiveness beyond removing immunities

        List<String> lines = readLines("call_modify_effectiveness_handler.s");
        battleOvl.writeCodeForceInline(lines, "CallModifyEffectivenessHandler");

        System.out.println("Set Call Modify Effectiveness Handler");
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

            if (q.isEmpty()) continue;

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
            if (poke.getStatByIndex(i) < bestStatValue) continue;

            if (poke.getStatByIndex(i) > bestStatValue) bestStats.clear();

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

        int abilityListAdditions = 30;

        // Move AbilityList
        relocateAbilityListRamAddress(abilityListAdditions);

        List<String> newText = Arrays.asList(new String[ParagonLiteAbilities.MAX - Abilities.teravolt]);

        Collections.fill(newText, "--");
        abilityNames.addAll(newText);

        Collections.fill(newText, " -");
        abilityDescriptions.addAll(newText);
        if (romHandler.isWhite2() || romHandler.isBlack2()) {
            String eggExplanation = abilityExplanations.remove(abilityExplanations.size() - 1);

            Collections.fill(newText, "");
            abilityExplanations.addAll(newText);

            abilityExplanations.add(eggExplanation);
        }

        int totalChanges = 77;
        int currentChanges = -1;
        long startTime = System.currentTimeMillis();
        System.out.println("setting abilities...");


        // ADDED ABILITIES

        // #168 Protean
        Utils.printProgress(totalChanges, ++currentChanges, "Protean");
        addProtean();

        // #169 Fur Coat
        Utils.printProgress(totalChanges, ++currentChanges, "Fur Coat");
        addFurCoat();

        // #171 Bulletproof
        Utils.printProgress(totalChanges, ++currentChanges, "Bulletproof");
        addBulletproof();

        // #172 Competitive
        Utils.printProgress(totalChanges, ++currentChanges, "Competitive");
        addCompetitive();

        // #173 Strong Jaw
        Utils.printProgress(totalChanges, ++currentChanges, "Strong Jaw");
        addStrongJaw();

        // #174 Refrigerate
        Utils.printProgress(totalChanges, ++currentChanges, "Refrigerate");
        addRefrigerate();

        // #177 Gale Wings
        Utils.printProgress(totalChanges, ++currentChanges, "Gale Wings");
        addGaleWings();

        // #178 Mega Launcher
        Utils.printProgress(totalChanges, ++currentChanges, "Mega Launcher");
        addMegaLauncher();

        // #181 Tough Claws
        Utils.printProgress(totalChanges, ++currentChanges, "Tough Claws");
        addToughClaws();

        // #182 Pixilate
        Utils.printProgress(totalChanges, ++currentChanges, "Pixilate");
        addPixilate();

        // #183 Gooey
        Utils.printProgress(totalChanges, ++currentChanges, "Gooey");
        addGooey();

        // #184 Aerilate
        Utils.printProgress(totalChanges, ++currentChanges, "Aerilate");
        addAerilate();

        // #192 Stamina
        Utils.printProgress(totalChanges, ++currentChanges, "Stamina");
        addStamina();

        // #200 Steelworker
        Utils.printProgress(totalChanges, ++currentChanges, "Steelworker");
        addSteelworker();

        // #202 Slush Rush
        Utils.printProgress(totalChanges, ++currentChanges, "Slush Rush");
        addSlushRush();

        // #205 Triage
        Utils.printProgress(totalChanges, ++currentChanges, "Triage");
        addTriage();

        // #206 Galvanize
        Utils.printProgress(totalChanges, ++currentChanges, "Galvanize");
        addGalvanize();

        // #218 Fluffy
        Utils.printProgress(totalChanges, ++currentChanges, "Fluffy");
        addFluffy();

        // #238 Cotton Down
        Utils.printProgress(totalChanges, ++currentChanges, "Cotton Down");
        addCottonDown();


        // Larger than max byte size...

        // #274 Wind Rider
        Utils.printProgress(totalChanges, ++currentChanges, "Wind Rider");
        addWindRider();

        // #277 Wind Power
        Utils.printProgress(totalChanges, ++currentChanges, "Wind Power");
        addWindPower();

        // #292 Sharpness
        Utils.printProgress(totalChanges, ++currentChanges, "Sharpness");
        addSharpness();

        // #293 Supreme Overlord
        Utils.printProgress(totalChanges, ++currentChanges, "Supreme Overlord");
        addSupremeOverlord();


        // Original Abilities...

        // #500 Heavy Wing
        Utils.printProgress(totalChanges, ++currentChanges, "Heavy Wing");
        addHeavyWing();

        // #501 Specialized (old Adaptability)
        Utils.printProgress(totalChanges, ++currentChanges, "Specialized");
        addSpecialized();

        // #502 Insectivore
        Utils.printProgress(totalChanges, ++currentChanges, "Insectivore");
        addInsectivore();

        // #503 Prestige
        Utils.printProgress(totalChanges, ++currentChanges, "Prestige");
        addPrestige();

        // #504 Lucky Foot
        Utils.printProgress(totalChanges, ++currentChanges, "Lucky Foot");
        addLuckyFoot();

        // #505 Assimilate
        Utils.printProgress(totalChanges, ++currentChanges, "Assimilate");
        addAssimilate();

        // #506 Stone Home
        Utils.printProgress(totalChanges, ++currentChanges, "Stone Home");
        addStoneHome();

        // #507 Cacophony
        Utils.printProgress(totalChanges, ++currentChanges, "Cacophony");
        addCacophony();


        // OLD ABILITIES

        // #001 Stench
        Utils.printProgress(totalChanges, ++currentChanges, "Stench");
        setStench();

        // #006 Damp
        Utils.printProgress(totalChanges, ++currentChanges, "Damp");
        setDamp();

        // #007 Limber (+ no speed drop)
        Utils.printProgress(totalChanges, ++currentChanges, "Limber");
        setLimber();

        // #012 Oblivious
        Utils.printProgress(totalChanges, ++currentChanges, "Oblivious");
        setOblivious();

        // #013 Cloud Nine
        Utils.printProgress(totalChanges, ++currentChanges, "Cloud Nine");
        setCloudNine();

        // #014 Compoundeyes -> Compound Eyes
        Utils.printProgress(totalChanges, ++currentChanges, "Compound Eyes");
        setCompoundEyes();

        // #016 Color Change
        Utils.printProgress(totalChanges, ++currentChanges, "Color Change");
        setColorChange();

        // #017 Immunity (+ Poison-type immunity)
        Utils.printProgress(totalChanges, ++currentChanges, "Immunity");
        setImmunity();

        // #023 Shadow Tag
        Utils.printProgress(totalChanges, ++currentChanges, "Shadow Tag");
        setShadowTag();

        // #025 Wonder Guard
        Utils.printProgress(totalChanges, ++currentChanges, "Wonder Guard");
        setWonderGuard();

        // #031 Lightningrod -> Lightning Rod
        Utils.printProgress(totalChanges, ++currentChanges, "Lightning Rod");
        setLightningRod();

        // #035 Illuminate
        Utils.printProgress(totalChanges, ++currentChanges, "Illuminate");
        addIlluminate();

        // #037 Huge Power (1.5x Attack)
        Utils.printProgress(totalChanges, ++currentChanges, "Huge Power");
        setHugePower();

        // #040 Magma Armor (Water/Ground resist + 10% chance to burn on contact)
        Utils.printProgress(totalChanges, ++currentChanges, "Magma Armor");
        setMagmaArmor();

        // #041 Water Veil
        Utils.printProgress(totalChanges, ++currentChanges, "Magma Armor");
        setWaterVeil();

        // #042 Magnet Pull
        Utils.printProgress(totalChanges, ++currentChanges, "Magnet Pull");
        setMagnetPull();

        // #045 Sand Stream (+ no sandstorm damage)
        Utils.printProgress(totalChanges, ++currentChanges, "Sand Stream");
        setSandStream();

        // #052 Hyper Cutter
        Utils.printProgress(totalChanges, ++currentChanges, "Hyper Cutter");
        setHyperCutter();

        // #055 Hustle (Moves with BP 60 or under gain +1 priority)
        Utils.printProgress(totalChanges, ++currentChanges, "Hustle");
        setHustle();

        // #057 Plus (4/3 ally Sp. Atk)
        Utils.printProgress(totalChanges, ++currentChanges, "Plus");
        setPlus();

        // #058 Minus (4/3 ally Sp. Def)
        Utils.printProgress(totalChanges, ++currentChanges, "Minus");
        setMinus();

        // #061 Shed Skin
        Utils.printProgress(totalChanges, ++currentChanges, "Shed Skin");
        setShedSkin();

        // #071 Arena Trap
        Utils.printProgress(totalChanges, ++currentChanges, "Arena Trap");
        setArenaTrap();

        // #072 Vital Spirit (boosts Sp. Def on hit)
        Utils.printProgress(totalChanges, ++currentChanges, "Vital Spirit");
        setVitalSpirit();

        // #073 White Smoke
        Utils.printProgress(totalChanges, ++currentChanges, "White Smoke");
        setWhiteSmoke();

        // #074 Pure Power (1.5x Sp. Atk)
        Utils.printProgress(totalChanges, ++currentChanges, "Pure Power");
        setPurePower();

        // #079 Rivalry (1.0x opposite gender, 1.2x same gender)
        Utils.printProgress(totalChanges, ++currentChanges, "Rivalry");
        setRivalry();

        // #083 Anger Point (Boost Attack on miss, crit, or flinch)
        Utils.printProgress(totalChanges, ++currentChanges, "Anger Point");
        setAngerPoint();

        // #089 Iron Fist (1.2x -> 1.3x)
        Utils.printProgress(totalChanges, ++currentChanges, "Iron Fist");
        setIronFist();

        // #091 Adaptability
        Utils.printProgress(totalChanges, ++currentChanges, "Adaptability");
        setAdaptability();

        // #094 Solar Power (1.5x -> 1.3x Sp. Atk; 1/8 -> 1/10 HP per turn)
        Utils.printProgress(totalChanges, ++currentChanges, "Solar Power");
        setSolarPower();

        // #101 Technician (<=60 -> <60)
        Utils.printProgress(totalChanges, ++currentChanges, "Technician");
        setTechnician();

        // #102 Leaf Guard (2/3x damage received in sun)
        Utils.printProgress(totalChanges, ++currentChanges, "Leaf Guard");
        setLeafGuard();

        // #105 Super Luck
        Utils.printProgress(totalChanges, ++currentChanges, "Super Luck");
        setSuperLuck();

        // #112 Slow Start
        Utils.printProgress(totalChanges, ++currentChanges, "Slow Start");
        setSlowStart();

        // #115 Ice Body (+ Ice-type immunity)
        Utils.printProgress(totalChanges, ++currentChanges, "Ice Body");
        setIceBody();

        // #117 Snow Warning (+ no hail damage)
        Utils.printProgress(totalChanges, ++currentChanges, "Snow Warning");
        setSnowWarning();

        // #119 Frisk -> X-ray Vision
        Utils.printProgress(totalChanges, ++currentChanges, "X-ray Vision");
        setXrayVision();

        // #131 Healer
        Utils.printProgress(totalChanges, ++currentChanges, "Healer");
        setHealer();

        // #132 Friend Guard (25% -> 20% reduction)
        Utils.printProgress(totalChanges, ++currentChanges, "Friend Guard");
        setFriendGuard();

        // #133 Weak Armor
        Utils.printProgress(totalChanges, ++currentChanges, "Weak Armor");
        setWeakArmor();

        // #134 Heavy Metal (+ 1.2x Defense, 0.9x Speed)
        Utils.printProgress(totalChanges, ++currentChanges, "Heavy Metal");
        setHeavyMetal();

        // #135 Light Metal (+ 0.9x Defense, 1.2x Speed)
        Utils.printProgress(totalChanges, ++currentChanges, "Light Metal");
        setLightMetal();

        // #142 Overcoat (+ immunity to spore moves)
        Utils.printProgress(totalChanges, ++currentChanges, "Overcoat");
        setOvercoat();

        // #144 Regenerator
        Utils.printProgress(totalChanges, ++currentChanges, "Regenerator");
        setRegenerator();

        // #154 Justified (+ immunity to Dark-type moves)
        Utils.printProgress(totalChanges, ++currentChanges, "Justified");
        setJustified();

        // #157 Sap Sipper -> Herbivore
        Utils.printProgress(totalChanges, ++currentChanges, "Herbivore");
        setHerbivore();

        // #163 Turbo Blaze (Fire-type attacks are always effective)
//        setTurboblaze();


        Utils.printProgressFinished(startTime, totalChanges);

        // Fixes ALL weather ability duration (normally it's indefinite)
        int weatherAbilityAddress = battleOvl.find("FF 20 48 71 28 1C");
        battleOvl.writeByte(weatherAbilityAddress, 0x05);

        // Changes Blaze/Torrent/Overgrow/Swarm
        setLowHpTypeBoostAbility();

        System.out.println("Set abilities");
    }

    private void addProtean() {
        int number = Abilities.protean;

        // Name
        abilityNames.set(number, "Protean");

        // Description
        abilityDescriptions.set(number, "Changes the Pokémon's\\xFFFEtype to match its move.");

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.unknown29, "protean.s"));
    }

    private void addFurCoat() {
        int number = Abilities.furCoat;

        // Name
        abilityNames.set(number, "Fur Coat");

        switch (mode) {
            case ParagonLite -> {
                // Description
                abilityDescriptions.set(number, "Reduces the damage\\xFFFEfrom physical moves.");

                // Data
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "fur_coat.s"));
            }
            case Redux -> {
                // Description
                abilityDescriptions.set(number, "Halves the damage\\xFFFEfrom physical moves.");

                // Data
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "redux_fur_coat.s"));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void addBulletproof() {
        int number = Abilities.bulletproof;

        // Name
        abilityNames.set(number, "Bulletproof");

        // Description
        abilityDescriptions.set(number, "Protects the Pokémon from\\xFFFEsome ball and bomb moves.");

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "bulletproof.s"));
    }

    private void addCompetitive() {
        int index = Abilities.competitive;

        // Name
        abilityNames.set(index, "Competitive");

        // Description
        String description = abilityDescriptions.get(Abilities.defiant).replace("Attack", "Sp. Atk");
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
        int index = Abilities.strongJaw;

        // Name
        abilityNames.set(index, "Strong Jaw");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist).replace("punching", "biting");
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

    private void addRefrigerate() {
        int index = Abilities.refrigerate;

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

    private void addGaleWings() {
        int index = Abilities.galeWings;

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

    private void addMegaLauncher() {
        int number = Abilities.megaLauncher;

        // Name
        abilityNames.set(number, "Mega Launcher");

        // Description
        abilityDescriptions.set(number, "Powers up aura, pulse,\\xFFFEball, and bomb moves.");

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "mega_launcher.s"));
    }

    private void addToughClaws() {
        int index = Abilities.toughClaws;

        // Name
        abilityNames.set(index, "Tough Claws");

        // Description
        String description = "Powers up moves that\\xFFFEmake direct contact.";
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Iron Fist, huh...\uF000븁\\x0000\\xFFFE"
                    + "This Ability increases the power of\\xFFFEmoves that make direct contact\uF000븀\\x0000\\xFFFE"
                    + "with the target.";
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "tough_claws"));
    }

    private void addPixilate() {
        int index = Abilities.pixilate;

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

    private void addGooey() {
        int number = Abilities.gooey;

        // Name
        abilityNames.set(number, "Gooey");

        // Description
        abilityDescriptions.set(number, "Contact moves lower the\\xFFFEattacker's Speed stat.");

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onHit2, "gooey.s"));
    }

    private void addAerilate() {
        int index = Abilities.aerilate;

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

    private void addStamina() {
        int index = Abilities.stamina;

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

    private void addSteelworker() {
        int number = Abilities.steelworker;

        // Name
        abilityNames.set(number, "Steel Worker");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist).replace("punching", "Steel-type");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Steelworker")
                    .replace("moves that punch", "Steel-type moves");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "steelworker.s"));
    }

    private void addSlushRush() {
        int number = Abilities.slushRush;

        // Name
        abilityNames.set(number, "Slush Rush");

        // Description
        String description = abilityDescriptions.get(Abilities.snowCloak).replace("evasion", "Speed");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.snowCloak)
                    .replace("Snow Cloak", "Slush Rush")
                    .replace("evasiveness", "Speed");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetSpeedStat, "slush_rush_speed.s"),
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "slush_rush_weather_immune.s"));
    }

    private void addTriage() {
        int number = Abilities.triage;

        // Name
        abilityNames.set(number, "Triage");

        // Description
        String description = abilityDescriptions.get(Abilities.prankster).replace("status", "healing");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.prankster)
                    .replace("Prankster", "Triage")
                    .replace("status moves", "moves that heal");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetPriority, "triage"));
    }

    private void addGalvanize() {
        int number = Abilities.galvanize;

        // Name
        abilityNames.set(number, "Galvanize");

        // Description
        String description = "Normal-type moves become\\xFFFEElectric-type moves.";
        abilityDescriptions.set(number, description);

        // TODO: Explanation
        if (abilityExplanations != null) {
            String explanation = "";
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "galvanize_type.s"),
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "common_move_type_change_power.s"));
    }

    private void addFluffy() {
        int number = Abilities.fluffy;

        // Name
        abilityNames.set(number, "Fluffy");

        // Data
        String filename;
        switch (mode) {
            case ParagonLite -> filename = "fluffy.s";
            case Redux -> filename = "redux_fluffy.s";
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDamage, filename));
    }

    private void addCottonDown() {
        int number = Abilities.cottonDown;

        // Name
        abilityNames.set(number, "Cotton Down");

        // TODO
    }

    private void addWindRider() {
        int index = Abilities.windRider;

        // Name
        abilityNames.set(index, "Wind Rider");

        // Description
        String description = "Boosts Attack when hit\\xFFFEby a wind move.";
        abilityDescriptions.set(index, description);

        // Data
        setAbilityEventHandlers(index,
                new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "wind_rider_immunity.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "wind_rider_on_enter.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "wind_rider_on_enter.s"),
                new AbilityEventHandler(Gen5BattleEventType.OnMoveSuccess, "wind_rider_after_tailwind.s"));
    }

    private void addWindPower() {
        int number = Abilities.windPower;

        // Name
        abilityNames.set(number, "Wind Power");

        // TODO
    }

    private void addSharpness() {
        int index = Abilities.sharpness;

        // Name
        abilityNames.set(index, "Sharpness");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist).replace("punching", "slicing");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Sharpness")
                    .replace("moves that punch", "moves that slice");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "sharpness.s"));
    }

    private void addSupremeOverlord() {
        int number = Abilities.supremeOverlord;

        // Name
        abilityNames.set(number, "SupremeOverlord");

        // Description
        abilityDescriptions.set(number, "Fainted allies boost\\xFFFEthe power of moves.");

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "supreme_overlord_power.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "supreme_overlord_on_enter.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "supreme_overlord_on_enter.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeExit, "supreme_overlord_on_exit.s"));
    }

    private void addHeavyWing() {
        int number = ParagonLiteAbilities.heavyWing;
        abilityUpdates.put(number, "Powers up Flying-type moves by 1.5x");

        // Name
        abilityNames.set(number, "Heavy Wing");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist).replace("punching", "Flying-type");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Heavy Wing")
                    .replace("moves that punch", "Flying-type moves");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "heavy_wing.s"));
    }

    private void addSpecialized() {
        int number = ParagonLiteAbilities.specialized;
        abilityUpdates.put(number, "NEW: STAB becomes 2.0x");

        // Name
        abilityNames.set(number, "Specialized");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.adaptability).replace("Adaptability", "Specialized");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetStab, Abilities.adaptability));
    }

    private void addInsectivore() {
        int index = ParagonLiteAbilities.insectivore;

        // Name
        abilityNames.set(index, "Insectivore");

        // Description
        String description = abilityDescriptions.get(Abilities.waterAbsorb).replace("Water", "Bug");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.waterAbsorb)
                    .replace("Water Absorb", "Insectivore")
                    .replace("Water", "Bug");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "insectivore.s"));
    }

    private void addPrestige() {
        int index = ParagonLiteAbilities.prestige;

        // Name
        abilityNames.set(index, "Prestige");

        // Description
        String description = abilityDescriptions.get(Abilities.moxie).replace("Attack", "Sp. Atk");
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
        String description = abilityDescriptions.get(Abilities.ironFist).replace("punching", "kicking");
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

    private void addAssimilate() {
        int index = ParagonLiteAbilities.assimilate;

        // Name
        abilityNames.set(index, "Assimilate");

        // Description
        String description = abilityDescriptions.get(Abilities.sapSipper).replace("Grass", "Psychic").replace("Attack", "Sp. Atk");
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
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "assimilate"));
    }

    private void addStoneHome() {
        int number = ParagonLiteAbilities.stoneHome;

        // Name
        abilityNames.set(number, "Stone Home");

        // Description
        abilityDescriptions.set(number, "Boosts the Defense stat\\xFFFEof allies.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "stone_home_defense"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "stone_home_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "stone_home_message"));
    }

    private void addCacophony() {
        int index = ParagonLiteAbilities.cacophony;

        // Name
        abilityNames.set(index, "Cacophony");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist).replace("punching", "sound");
        abilityDescriptions.set(index, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Cacophony")
                    .replace("moves that punch", "sound-based moves");
            abilityExplanations.set(index, explanation);
        }

        // Data
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "cacophony.s"));
    }

    private void setStench() {
        if (mode == Mode.Redux) {
            int number = Abilities.stench;
            setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetFlinch, "redux_stench.s"));
        }
    }

    private void setDamp() {
        int number = Abilities.damp;

        setAbilityEventHandlers(number,
                // Old Damp
                new AbilityEventHandler(Gen5BattleEventType.unknown1F),
                new AbilityEventHandler(Gen5BattleEventType.onFlinch),
                new AbilityEventHandler(Gen5BattleEventType.unknown03),
                new AbilityEventHandler(Gen5BattleEventType.unknown04),
                new AbilityEventHandler(Gen5BattleEventType.unknown6A),

                // Old Heatproof
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, Abilities.heatproof),
                new AbilityEventHandler(Gen5BattleEventType.unknown6B, Abilities.heatproof));
    }

    private void setLimber() {
        int number = Abilities.limber;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainStatus),
                new AbilityEventHandler(Gen5BattleEventType.unknown67),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.unknown02),
                new AbilityEventHandler(Gen5BattleEventType.unknown5B, "limber_speed.s"),
                new AbilityEventHandler(Gen5BattleEventType.unknown5C, "limber_speed_message.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "oblivious_taunt.s"),
                new AbilityEventHandler(Gen5BattleEventType.unknown02));
    }

    private void setCloudNine() {
//        int number = Abilities.cloudNine;
//
//        setAbilityEventHandlers(number,
//                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "cloud_nine.s"),
//                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "cloud_nine.s"));
    }

    private void setCompoundEyes() {
        int number = Abilities.compoundEyes;

        // Name
        abilityNames.set(number, "Compound Eyes");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(number).replace("Compoundeyes", "Compound Eyes");
            abilityExplanations.set(number, explanation);
        }
    }

    private void setColorChange() {
        if (mode != Mode.Redux) return;

        // TODO: Add bespoke Protean

        int number = Abilities.colorChange;
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.unknown29, "protean.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "immunity.s"),
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
                new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "wonder_guard_message.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "wonder_guard_message.s"));
    }

    private void setLightningRod() {
        int number = Abilities.lightningRod;

        // Name
        abilityNames.set(number, "Lightning Rod");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(number).replace("Lightningrod", "Lightning Rod");
            abilityExplanations.set(number, explanation);
        }
    }

    private void addIlluminate() {
        int number = Abilities.illuminate;

        // Description
        abilityDescriptions.set(number, "Ups resistance to Dark-\\xFFFEand Ghost-type moves.");

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "redux_illuminate.s"));
    }

    private void setHugePower() {
        if (mode == Mode.Redux) return;

        int number = Abilities.hugePower;

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Huge Power, huh...\uF000븁\\x0000\\xFFFEThis Ability increases a Pokémon's\\xFFFEAttack stat by half.\uF000븁\\x0000";
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "huge_power.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "huge_power_message.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "huge_power_message.s"));
    }

    private void setMagmaArmor() {
        int number = Abilities.magmaArmor;

        switch (mode) {
            case ParagonLite -> {
                // Description
                String description = "Resists Water- and\\xFFFEGround-type moves.";
                abilityDescriptions.set(number, description);

                // Explanation
                if (abilityExplanations != null) {
                    String explanation = "Magma Armor, huh...\uF000븁\\x0000\\xFFFE"
                            + "This Ability halves damage from\\xFFFEWater- and Ground-type moves.\uF000븁\\x0000"
                            + "It also has a small chance to inflict\\xFFFEthe burned status condition\uF000븀\\x0000\\xFFFE"
                            + "when hit with a direct attack.\uF000븁\\x0000\\xFFFE"
                            + "What's more...\uF000븁\\x0000\\xFFFE"
                            + "It makes Eggs in your party hatch faster.\uF000븁\\x0000";
                    abilityExplanations.set(number, explanation);
                }

                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onHit2, "magma_armor_burn.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "magma_armor_resist.s"));
            }
            case Redux -> setAbilityEventHandlers(number,
                    new AbilityEventHandler(Gen5BattleEventType.onPokeGainStatus),
                    new AbilityEventHandler(Gen5BattleEventType.unknown67),
                    new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                    new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                    new AbilityEventHandler(Gen5BattleEventType.unknown02),
                    new AbilityEventHandler(Gen5BattleEventType.onGetCrit, Abilities.battleArmor));
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setWaterVeil() {
        int number = Abilities.waterVeil;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainStatus),
                new AbilityEventHandler(Gen5BattleEventType.unknown67),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility), // TODO Add message
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter), // TODO Add message
                new AbilityEventHandler(Gen5BattleEventType.unknown02),
                new AbilityEventHandler(Gen5BattleEventType.onWeather, Abilities.overcoat));
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

    private void setHyperCutter() {
        int number = Abilities.hyperCutter;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.unknown5B, "hyper_cutter.s"),
                new AbilityEventHandler(Gen5BattleEventType.unknown5C));
    }

    private void setHustle() {
        int number = Abilities.hustle;

        switch (mode) {
            case ParagonLite: {
                abilityUpdates.put(number, "Low base power moves have increased priority");

                // Description
                String description = "Gives priority to the\\xFFFEPokémon's weaker moves.";
                abilityDescriptions.set(number, description);

                // Explanation
                if (abilityExplanations != null) {
                    String explanation = "Hustle, huh...\uF000븁\\x0000\\xFFFE"
                            + "Pokémon with this Ability can\\xFFFEuse weaker moves earlier\uF000븀\\x0000\\xFFFE"
                            + "than usual.\uF000븁\\x0000\\xFFFE"
                            + "What's more...\uF000븁\\x0000\\xFFFE"
                            + "It raises the chance to encounter\\xFFFEhigh-level wild Pokémon\uF000븀\\x0000\\xFFFE"
                            + "when the leading party member has it.\uF000븁\\x0000";
                    abilityExplanations.set(number, explanation);
                }

                // Data
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetPriority, "hustle.s"));
                break;
            }
            case Redux: {
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetPriority, "redux_hustle.s"));
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }

    }

    private void setPlus() {
        int number = Abilities.plus;

        // Description
        abilityDescriptions.set(number, "Boosts the Sp. Atk stat\\xFFFEof allies.");

        switch (mode) {
            case ParagonLite -> {
                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "plus_spatk.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "plus_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "plus_message.s"));
            }
            case Redux -> {
                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "redux_plus_spatk.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "plus_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "plus_message.s"));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setMinus() {
        int number = Abilities.minus;


        switch (mode) {
            case ParagonLite -> {
                // Description
                abilityDescriptions.set(number, "Boosts the Sp. Def stat\\xFFFEof allies.");

                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "minus_spdef.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "minus_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "minus_message.s"));
            }
            case Redux -> {
                // Description
                abilityDescriptions.set(number, "Boosts the Attack stat\\xFFFEof allies.");

                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "redux_minus_attack.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "minus_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "minus_message.s"));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setShedSkin() {
        int number = Abilities.shedSkin;

        // Description
        abilityDescriptions.set(number, "The Pokémon heals its\\xFFFEown status problems.");

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onPreTurn, "shed_skin.s"));
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
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onHit2, "vital_spirit.s"));
    }

    private void setWhiteSmoke() {
        int number = Abilities.whiteSmoke;

        // Description
        abilityDescriptions.set(number, "Prevents the Pokémon's\\xFFFEstats from dropping.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.unknown5B, "white_smoke.s"),
                new AbilityEventHandler(Gen5BattleEventType.unknown5C));
    }

    private void setPurePower() {
        int number = Abilities.purePower;

        // Description
        String description = abilityDescriptions.get(number).replace("Attack", "Sp. Atk");
        abilityDescriptions.set(number, description);

        switch (mode) {
            case ParagonLite -> {
                // Explanation
                if (abilityExplanations != null) {
                    String explanation = "Pure Power, huh...\uF000븁\\x0000\\xFFFE"
                            + "This Ability increases a Pokémon's\\xFFFESp. Atk stat by half.\uF000븁\\x0000";
                    abilityExplanations.set(number, explanation);
                }

                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "pure_power.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "pure_power_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "pure_power_message.s"));
            }
            case Redux -> {
                // Explanation
                if (abilityExplanations != null) {
                    String explanation = abilityExplanations.get(number).replace("Attack", "Sp. Atk");
                    abilityExplanations.set(number, explanation);
                }

                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "redux_pure_power.s"));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setRivalry() {
        int number = Abilities.rivalry;

        // Name
        if (abilityExplanations != null) {
            abilityExplanations.set(number, "Rivalry, huh...\uF000븁\\x0000\\xFFFE"
                    + "This Ability raises the power of\\xFFFEthe Pokémon's move when the target is\uF000븀\\x0000\\xFFFE"
                    + "of the same gender.\uF000븁\\x0000");
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
                new AbilityEventHandler(Gen5BattleEventType.OnMoveFail, "anger_point_miss"));
    }

    private void setIronFist() {
        setAbilityEventHandlers(Abilities.ironFist, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "iron_fist"));
    }

    private void setAdaptability() {
        int number = Abilities.adaptability;

        // Description
        String description = "Powers up moves not\\xFFFEof the same type.";
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = "Adaptability, huh...\uF000븁\\x0000\\xFFFE"
                    + "This ability gives a power boost to\\xFFFEmoves that move don't match\uF000븀\\x0000\\xFFFE"
                    + "the Pokémon's type.\uF000븁\\x0000";
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "adaptability.s"));
    }

    private void setSolarPower() {
        int number = Abilities.solarPower;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "solar_power_weather.s"),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "solar_power_spatk_boost.s"));
    }

    private void setTechnician() {
        if (mode == Mode.Redux)
            return;

        int number = Abilities.technician;
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "technician.s"));
    }

    private void setLeafGuard() {
        int number = Abilities.leafGuard;

        switch (mode) {
            case ParagonLite -> {
                // Description
                String description = "Reduces damage in\\xFFFEsunny weather.";
                abilityDescriptions.set(number, description);

                // Data
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "leaf_guard.s"));
            }
            case Redux -> {
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onPokeGainStatus),
                        new AbilityEventHandler(Gen5BattleEventType.unknown67),
                        new AbilityEventHandler(Gen5BattleEventType.unknown0E),
                        new AbilityEventHandler(Gen5BattleEventType.onGetCrit, Abilities.battleArmor));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setSuperLuck() {
        int number = Abilities.superLuck;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetCrit),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "super_luck_message.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "super_luck_message.s"));
    }

    private void setSlowStart() {
        int number = Abilities.slowStart;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.unknown58),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onGetSpeedStat),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue),
                new AbilityEventHandler(Gen5BattleEventType.onEndOfTurn2, "slow_start_end_of_turn.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "ice_body_immunity.s"));
    }

    private void setSnowWarning() {
        int number = Abilities.snowWarning;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "snow_warning_no_damage.s"));
    }

    private void setXrayVision() {
        int number = ParagonLiteAbilities.xrayVision;

        // Name
        abilityNames.set(number, "X-ray Vision");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.frisk).replace("Frisk", "X-ray Vision");
            abilityExplanations.set(number, explanation);
        }

        battleEventStrings2.set(439, "\uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
        battleEventStrings2.set(440, "The wild \uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
        battleEventStrings2.set(441, "The foe's \uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
    }

    private void setHealer() {
        if (mode == Mode.Redux)
            return;

        int number = Abilities.healer;

        abilityDescriptions.set(number, "Heals ally HP and\\xFFFEsometimes status condition.");

        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onEndOfTurn1, "healer_recover_hp.s"));
    }

    private void setFriendGuard() {
        int number = Abilities.friendGuard;

        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "friend_guard"));
    }

    private void setWeakArmor() {
        int number = Abilities.weakArmor;

        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onHit2, "weak_armor.s"));
    }

    private void setHeavyMetal() {
        int number = Abilities.heavyMetal;

        switch (mode) {
            case ParagonLite -> {
                // Description
                String description = "Boosts the Defense stat,\\xFFFEbut lowers the Speed stat.";
                abilityDescriptions.set(number, description);

                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "heavy_metal_defense"),
                        new AbilityEventHandler(Gen5BattleEventType.onGetSpeedStat, "heavy_metal_speed"));
            }
            case Redux -> {
                abilityDescriptions.set(number, abilityDescriptions.get(Abilities.filter));

                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetPokeWeight),
                        new AbilityEventHandler(Gen5BattleEventType.onGetDamage, Abilities.filter));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setLightMetal() {
        int number = Abilities.lightMetal;

        // Description
        String description = "Boosts the Speed stat, but\\xFFFElowers the Defense stat.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "light_metal_defense"),
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
                new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "overcoat_powder_immunity"));
    }

    private void setRegenerator() {
        int number = Abilities.regenerator;

        if (mode == Mode.Redux) {
            // Data
            setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onSwitchOut, "redux_regenerator.s"));
        }
    }

    private void setJustified() {
        int number = Abilities.justified;

        // Name
        abilityNames.set(number, "Justified");

        // Description
        String description = abilityDescriptions.get(Abilities.sapSipper).replace("Grass", "Dark");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.sapSipper).replace("Sap Sipper", "Justified").replace("Grass", "Dark");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onPreMoveImmuneCheck, "justified"));
    }

    private void setHerbivore() {
        int number = ParagonLiteAbilities.herbivore;

        // Name
        abilityNames.set(number, "Herbivore");

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(number).replace("Sap Sipper", "Herbivore");
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

    private void setLowHpTypeBoostAbility() {
        String filename;
        switch (mode) {
            case ParagonLite -> filename = "eventhandlers/ability/low_hp_type_boost.s";
            case Redux -> filename = "eventhandlers/ability/redux_low_hp_type_boost.s";
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
        List<String> lines = readLines(filename);

        battleOvl.writeCodeForceInline(lines, "CommonLowHPTypeBoostAbility");
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

        // All sound moves hit through substitute
        for (Move m : moves) {
            if (m != null && m.isSoundMove) m.bypassesSubstitute = true;
        }

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
        moves[Moves.nightSlash].isCustomSliceMove = true; // 400
        moves[Moves.airSlash].isCustomSliceMove = true; // 403
        moves[Moves.xScissor].isCustomSliceMove = true; // 404
        moves[Moves.psychoCut].isCustomSliceMove = true; // 427
        moves[Moves.crossPoison].isCustomSliceMove = true; // 440
        moves[Moves.sacredSword].isCustomSliceMove = true; // 533
        moves[Moves.razorShell].isCustomSliceMove = true; // 534
        moves[Moves.secretSword].isCustomSliceMove = true; // 548

        // Triage
        for (int i = 0; i <= Gen5Constants.moveCount; ++i) {
            if (moves[i] == null) continue;

            if (moves[i].isHealMove || moves[i].recoil > 0) moves[i].isCustomTriageMove = true;
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
        moves[Moves.ominousWind].isCustomWindMove = true; // 466
        moves[Moves.hurricane].isCustomWindMove = true; // 542

        // Ball/Bomb moves
        moves[Moves.eggBomb].isCustomBallBombMove = true; // 121
        moves[Moves.barrage].isCustomBallBombMove = true; // 140
        moves[Moves.sludgeBomb].isCustomBallBombMove = true; // 188
        moves[Moves.octazooka].isCustomBallBombMove = true; // 190
        moves[Moves.zapCannon].isCustomBallBombMove = true; // 192
        moves[Moves.shadowBall].isCustomBallBombMove = true; // 247
        moves[Moves.mistBall].isCustomBallBombMove = true; // 296
        moves[Moves.iceBall].isCustomBallBombMove = true; // 301
        moves[Moves.weatherBall].isCustomBallBombMove = true; // 311
        moves[Moves.bulletSeed].isCustomBallBombMove = true; // 331
        moves[Moves.rockBlast].isCustomBallBombMove = true; // 350
        moves[Moves.gyroBall].isCustomBallBombMove = true; // 360
        moves[Moves.auraSphere].isCustomBallBombMove = true; // 396
        moves[Moves.seedBomb].isCustomBallBombMove = true; // 402
        moves[Moves.focusBlast].isCustomBallBombMove = true; // 411
        moves[Moves.energyBall].isCustomBallBombMove = true; // 412
        moves[Moves.mudBomb].isCustomBallBombMove = true; // 426
        moves[Moves.rockWrecker].isCustomBallBombMove = true; // 439
        moves[Moves.magnetBomb].isCustomBallBombMove = true; // 443
        moves[Moves.electroBall].isCustomBallBombMove = true; // 486
        moves[Moves.acidSpray].isCustomBallBombMove = true; // 491
        moves[Moves.searingShot].isCustomBallBombMove = true; // 545

        // Pulse Moves
        moves[Moves.waterPulse].isCustomPulseMove = true; // 352
        moves[Moves.auraSphere].isCustomPulseMove = true; // 396
        moves[Moves.darkPulse].isCustomPulseMove = true; // 399
        moves[Moves.dragonPulse].isCustomPulseMove = true; // 406
        moves[Moves.healPulse].isCustomPulseMove = true; // 505

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

            if (q.isEmpty()) continue;

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
                    if (!value.startsWith("\"") || !value.endsWith("\"")) throw new RuntimeException(String.format("Value must be in quotations for %s: \"%s\"", key, value));
                    move.name = value.substring(1, value.length() - 1);
                    break;
                }
                case "Description": {
                    if (!value.startsWith("\"") || !value.endsWith("\"")) throw new RuntimeException(String.format("Value must be in quotations for %s: \"%s\"", key, value));
                    move.description = value.substring(1, value.length() - 1);
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
                    if (move.hasPerfectAccuracy()) move.accuracy = Move.getPerfectAccuracy();

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
                        String[] minMaxValue = value.substring(1, value.length() - 1).split(",", 2);
                        move.minHits = Integer.parseInt(minMaxValue[0].trim());
                        move.maxHits = Integer.parseInt(minMaxValue[1].trim());
                        break;
                    }

                    int hits = Integer.parseInt(value);
                    move.minHits = hits;
                    move.maxHits = hits;
                    break;
                }
                case "StatusType": {
                    move.statusType = MoveStatusType.valueOf(value);
                    if (move.statusType == MoveStatusType.NONE) move.statusPercentChance = 0;

                    break;
                }
                case "StatusChance": {
                    move.statusPercentChance = Integer.parseInt(value);
                    break;
                }
                case "StatusTurns": {
                    if (value.startsWith("[") && value.endsWith("]")) {
                        String[] minMaxValue = value.substring(1, value.length() - 1).split(",", 2);
                        move.statusMinTurns = Integer.parseInt(minMaxValue[0].trim());
                        move.statusMaxTurns = Integer.parseInt(minMaxValue[1].trim());
                        break;
                    }

                    int turns = Integer.parseInt(value);
                    move.statusMinTurns = turns;
                    move.statusMaxTurns = turns;
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
                case "IsPunch": {
                    switch (value) {
                        case "TRUE":
                            move.isPunchMove = true;
                            break;
                        case "FALSE":
                            move.isPunchMove = false;
                            break;
                        default:
                            throw new RuntimeException(String.format("Unknown flag value for %s: \"%s\"", key, value));
                    }

                    break;
                }
                case "BypassesSubstitute": {
                    switch (value) {
                        case "TRUE":
                            move.bypassesSubstitute = true;
                            break;
                        case "FALSE":
                            move.bypassesSubstitute = false;
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

        int[] newMoves;
        int[] movesToClear;
        switch (mode) {
            case ParagonLite -> {
                newMoves = new int[]{
                        Moves.eggBomb, // #121
                        Moves.astonish, // #310
                };

                movesToClear = new int[]{
                        Moves.dragonRage, // #082
                        Moves.nightShade, // #101
                        Moves.psywave, // #149
                        Moves.rollout, // #205
                        Moves.memento, // #262
                        Moves.iceBall, // #301
                        Moves.chatter, // #448
                };
            }
            case Redux -> {
                newMoves = new int[]{
                        Moves.eggBomb, // #121
                };

                movesToClear = new int[]{
                        Moves.chatter, // #448
                };
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }


        for (int moveToClear : movesToClear) {
            clearMoveEventHandlers(moveToClear);
        }

        // TODO: For some reason the relocator addresses values are wrong
        relocateMoveListRamAddress(newMoves.length - movesToClear.length);

        // + #121 Egg Bomb
        cloneMoveEventHandlers(Moves.eggBomb, Moves.psystrike);

        // + #310 Astonish
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.astonish, Moves.fakeOut);


        // TODO #564 Sticky Web
        // TODO #565 Fell Stinger

        // TODO: Make this work for AI
        // + #573 Freeze-Dry
//        setMoveEventHandlers(Moves.freezeDry, new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "freeze-dry.s"));

        // TODO #668 Strength Sap

        // + #676 Pollen Puff
//        setMoveEventHandlers(Moves.pollenPuff,
//                new MoveEventHandler(Gen5BattleEventType.onMoveDetermineMode, "pollen_puff_set_mode.s"),
//                new MoveEventHandler(Gen5BattleEventType.unknown31, "pollen_puff_heal.s"));

        // TODO #694 Aurora Veil

        // + #776 Body Press
//        setMoveEventHandlers(Moves.bodyPress, new MoveEventHandler(Gen5BattleEventType.onGetAttackingStat, "body_press.s"));

        // + #813 Triple Axel
//        switch (mode) {
//            case ParagonLite -> setMoveEventHandlers(Moves.tripleAxel,
//                    new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "triple_kick.s"),
//                    new MoveEventHandler(Gen5BattleEventType.onGetMultiStrike, Moves.tripleKick));
//            case Redux -> setMoveEventHandlers(Moves.tripleAxel,
//                    new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "redux_triple_axel.s"),
//                    new MoveEventHandler(Gen5BattleEventType.onGetMultiStrike, Moves.tripleKick));
//            default -> throw new IllegalStateException("Unexpected value: " + mode);
//        }

        // TODO #861 Ice Spinner


        // #167 Triple Kick
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.tripleKick, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "triple_kick.s"));

        // #200 Outrage
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.outrage, Moves.revenge);

        // #237 Hidden Power
        setMoveEventHandlers(Moves.hiddenPower, new MoveEventHandler(Gen5BattleEventType.onGetMoveType));

        // #243 Mirror Coat
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.mirrorCoat, Moves.eruption);

        // #360 Gyro Ball
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.gyroBall, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "gyro_ball.s"));

        // #362 Brine
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.brine, new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "brine.s"));

        // #368 Metal Burst
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.metalBurst, Moves.eruption);

        // #381 Lucky Chant
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.luckyChant, Moves.focusEnergy);

        // #449 Judgment (needs update for Pixie plate)
        setMoveEventHandlers(Moves.judgment, new MoveEventHandler(Gen5BattleEventType.onGetMoveType, "judgment.s"));

        // #486 Electro Ball
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.electroBall, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "electro_ball.s"));

        System.out.println("Set moves");
    }

    public void setItems() {
        registerItemEffects();

        updateNaturalGiftPowers();

        relocateItemListRamAddress(9);

        // Weather change item
        if (mode == Mode.ParagonLite) {
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
        }

        // #581 Big Nugget
        setBigNugget();

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
            if (itemData[0x07] > 0) itemData[0x07] += 30;
        }
    }

    void setIcyRock() {
        int index = Items.icyRock;

        itemDescriptions.set(index, "An item to be held by a Pokémon.\\xFFFEThis rock summons a hailstorm\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onPokeEnter, "icy_rock_hail"), new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "icy_rock_hail"), new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "icy_rock_immune"));
    }

    void setSmoothRock() {
        int number = Items.smoothRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock summons a sandstorm\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onPokeEnter, "smooth_rock_sandstorm"), new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "smooth_rock_sandstorm"), new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "smooth_rock_immune"));
    }

    void setHeatRock() {
        int number = Items.heatRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock turns the sunlight harsh\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onPokeEnter, "heat_rock"), new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "heat_rock"));
    }

    void setDampRock() {
        int number = Items.dampRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock makes it rain\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onPokeEnter, "damp_rock"), new ItemEventHandler(Gen5BattleEventType.onPokeGainItem, "damp_rock"));
    }

    void setBigNugget() {
        setItemFlingPower(Items.bigNugget, 130);
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

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onHit2, "weakness_policy_on_hit"), new ItemEventHandler(Gen5BattleEventType.unknown72, "weakness_policy_boost"));
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

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onGetDamage, "roseli_berry_super_effective_check"), new ItemEventHandler(Gen5BattleEventType.unknown44, Items.occaBerry));
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

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onPreMove, "fairy_gem_work"), new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "fairy_gem_damage_boost"), new ItemEventHandler(Gen5BattleEventType.unknown88, Items.fireGem));
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

        setItemEventHandlers(index, new ItemEventHandler(0x5B, "clear_amulet_5B"), new ItemEventHandler(0x5C, "clear_amulet_5C"));
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

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onGetMultiStrike, "loaded_dice"));
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
        if (price % 10 != 0) throw new RuntimeException("Price must be a multiple of 10");

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

        if (type == null) data[0x08] |= (byte) 0b00011111;
    }

    // 0x08 [6]
    private void setCanRegister(int itemNumber, boolean canRegister) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        if (canRegister) data[0x08] |= (byte) 0b01000000;
        else data[0x08] &= (byte) 0b10111111;
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
            poke1.pokemon = romHandler.getPokemon().get(Species.jellicent);
            poke1.level = 100;
            pokes[Species.jellicent].ability1 = Abilities.battleArmor;
            poke1.abilitySlot = 1;
            poke1.moves = new int[]{Moves.glaciate, 0, 0, 0};
            poke1.heldItem = Items.wideLens;
            poke1.IVs = 0;

            if (tr.pokemon.size() < 2)
                tr.pokemon.add(tr.pokemon.get(0).copy());
//            TrainerPokemon poke2 = tr.pokemon.get(1);
//            poke2.pokemon = romHandler.getPokemon().get(Species.carracosta);
//            poke2.level = 100;
//            pokes[Species.lanturn].ability1 = Abilities.voltAbsorb;
//            poke2.abilitySlot = 1;
//            poke2.moves = new int[]{Moves.recover, 0, 0, 0};
//            poke2.IVs = 0;
//            poke2.heldItem = Items.rindoBerry;
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
        int critChanceAddress = battleOvl.find(Gen5Constants.critChanceLocator);
        if (critChanceAddress < 0) critChanceAddress = battleOvl.find("1808020101");

        battleOvl.writeByte(critChanceAddress, 0);

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

            if (objectNumber == 0) continue;

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
            if (!globalAddressMap.isValidLabel(battleOvl, redirectorLabel)) globalAddressMap.registerCodeAddress(battleOvl, redirectorLabel, redirectorAddress, 2);

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

    private void relocateObjectList(String label, int oldAddress, int oldCount, int newCount, int effectListAddressDataAddress, int effectListAddress4DataAddress) {
        int newSize = newCount * 8;
        byte[] data = new byte[newSize];

        int writeIndex = 0;
        int readIndex = 0;
        while (readIndex < oldCount) {
            int number = battleOvl.readWord(oldAddress + readIndex * 8);
            int redirectorAddress = battleOvl.readWord(oldAddress + readIndex * 8 + 4);

            if (number == 0) {
                if (redirectorAddress != 0) throw new RuntimeException("If the number was 0, we should expect the redirector address to also be 0");

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
            if ((instruction & 0xFF00) == 0x4700) break;

            // POP
            if ((instruction & 0xFE00) == 0xBC00) break;
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
            if ((instruction & 0xFF00) == 0x4700) break;

            // POP
            if ((instruction & 0xFE00) == 0xBC00) break;
        }

        return returnValue;
    }

    private int getEventHandlerListCountFromRedirector(int redirectorAddress) {
        int countSetAddress = getRedirectorCountSetAddress(redirectorAddress);
        if (countSetAddress <= 0) throw new RuntimeException();

        return battleOvl.readUnsignedByte(countSetAddress);
    }

    private int getEventHandlerListAddressFromRedirector(int redirectorAddress) {
        int eventListReferenceAddress = getRedirectorListReferenceAddress(redirectorAddress);
        if (eventListReferenceAddress <= 0) throw new RuntimeException();

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

            String fileName = fullFuncName;
            if (!fileName.endsWith(".s"))
                fileName += ".s";

            if (!globalAddressMap.isValidLabel(battleOvl, fullFuncName)) {
                List<String> lines = readLines(String.format("eventhandlers/%s/%s", getFuncDirectory(), fileName));

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

        AbilityEventHandler(int type, String filename) {
            setFromFuncName(type, filename);
        }

        AbilityEventHandler(int type, int existingAbility) {
            this.type = type;

            int referenceAddress = getEventHandlerFuncReferenceAddress(existingAbility, getAbilityListAddress(), getAbilityListCount(), type);
            this.address = battleOvl.readWord(referenceAddress) - 1;
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

        MoveEventHandler(int type, int existingMove) {
            this.type = type;

            int referenceAddress = getEventHandlerFuncReferenceAddress(existingMove, getMoveListAddress(), getMoveListCount(), type);
            this.address = battleOvl.readWord(referenceAddress) - 1;
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
            if (eventHandler.address > 0) continue;

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
        List<String> lines = Arrays.asList("mov r1, #" + eventHandlers.length, "str r1, [r0]", "ldr r0, =" + eventHandlerListAddress, "bx lr");
        int redirectorFuncAddress = battleOvl.writeCodeUnnamed(lines);

        // Write to object list
        battleOvl.writeWord(objectListAddress + index * 8, number, false);
        battleOvl.writeWord(objectListAddress + index * 8 + 4, redirectorFuncAddress + 1, true);
    }

    private void setAbilityEventHandlers(int abilityNumber, AbilityEventHandler... events) {
        if (abilityNumber > ParagonLiteAbilities.MAX)
            throw new IndexOutOfBoundsException(String.format("Ability index %d does not exist", abilityNumber));

        int abilityListRamAddress = getAbilityListAddress();
        int abilityListCount = getAbilityListCount();
        int index = getBattleObjectIndex(abilityListRamAddress, abilityListCount, abilityNumber);
        setBattleObject(abilityNumber, index, abilityListRamAddress, events);
    }

    private void cloneAbilityEventHandlers(int abilityNumber, int otherAbilityNumber) {
        int abilityListAddress = getAbilityListAddress();
        int listIndex = getBattleObjectIndex(abilityListAddress, getAbilityListCount(), abilityNumber);
        int abilityRedirectorAddress = getAbilityRedirectorAddress(otherAbilityNumber);
        battleOvl.writeWord(abilityListAddress + listIndex * 8, abilityNumber, false);
        battleOvl.writeWord(abilityListAddress + listIndex * 8 + 4, abilityRedirectorAddress + 1, true);
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
        if (!fullFuncName.contains("::")) throw new RuntimeException(String.format("Could not find function \"%s\".", fullFuncName));

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
            if (poke == null) continue;

            if (poke.baseForme == null || poke.baseForme == poke) continue;

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
                if (!pokeUpdate.hasAnyUpdate(oldAbilityNames, abilityNames)) continue;

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
            filename = "paragonlite/" + filename;
            InputStream stream = FileFunctions.openConfig(filename);
            if (stream == null)
                throw new RuntimeException(String.format("Could not find file \"%s\"", filename));
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
        if (!sprite.endsWith(extension)) sprite += extension;

        byte[] bytes;
        try {
            InputStream stream = FileFunctions.openConfig(String.format("paragonlite/itemsprites/%s", sprite));
            bytes = new byte[stream.available()];
            if (stream.read(bytes) == -1) throw new IOException();
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
            if (stream.read(bytes) == -1) throw new IOException();
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
