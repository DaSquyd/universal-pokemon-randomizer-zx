package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.BitmapFile;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.GFXFunctions;
import com.dabomstew.pkrandom.Utils;
import com.dabomstew.pkrandom.arm.ArmDecoder;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.DataFormatException;

public class ParagonLiteHandler {

    public static class Params {
        Gen5RomHandler romHandler;
        Gen5RomHandler.RomEntry romEntry;

        byte[] arm9Data;

        Pokemon[] pokes;
        List<Move> moves;

        NARCArchive pokemonGraphicsNarc;
        NARCArchive moveAnimationsNarc;
        NARCArchive itemDataNarc;
        NARCArchive itemGraphicsNarc;
        NARCArchive moveAnimationScriptsNarc;
        NARCArchive battleAnimationScriptsNarc;
        NARCArchive battleUIGraphicsNarc;
        NARCArchive trainerAIScriptsNarc;

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
    }

    public enum Mode {
        ParagonLite, Redux
    }

    public static Mode mode = Mode.Redux;

    Gen5RomHandler romHandler;

    ArmParser armParser;
    ParagonLiteAddressMap globalAddressMap;

    ParagonLiteOverlay arm9;
    ParagonLiteOverlay battleOvl;
    ParagonLiteOverlay BattleLevelOvl;
    ParagonLiteOverlay battleServerOvl;
    ParagonLiteOverlay trainerAIOvl;
    ParagonLiteOverlay storageOvl;

    Pokemon[] pokes;
    List<Move> moves;

    NARCArchive pokemonGraphicsNarc;
    NARCArchive moveAnimationsNarc;
    NARCArchive itemDataNarc;
    NARCArchive itemGraphicsNarc;
    NARCArchive moveAnimationScriptsNarc;
    NARCArchive battleAnimationScriptsNarc;
    NARCArchive battleUIGraphicsNarc;
    NARCArchive trainerAIScriptsNarc;

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

    ParagonLiteHandler(Params params) {
        romHandler = params.romHandler;

        Gen5RomHandler.RomEntry romEntry = params.romEntry;

        int battleOvlNumber = romEntry.getInt("BattleOvlNumber");
        int BattleLevelOvlNumber = romEntry.getInt("BattleLevelOvlNumber");
        int battleServerOvlNumber = romEntry.getInt("BattleServerOvlNumber");
        int trainerAIOvlNumber = romEntry.getInt("TrainerAIOvlNumber");
        int storageOvlNumber = romEntry.getInt("PCOvlNumber");

        globalAddressMap = new ParagonLiteAddressMap();
        armParser = new ArmParser(globalAddressMap);

        try {
            arm9 = new ParagonLiteArm9(romHandler, params.arm9Data, armParser, globalAddressMap);

            byte[] battleOvlData = romHandler.readOverlay(battleOvlNumber);
            int battleOvlAddress = romHandler.getOverlayAddress(battleOvlNumber);
            battleOvl = new ParagonLiteOverlay(romHandler, battleOvlNumber, "Battle", battleOvlData, battleOvlAddress, ParagonLiteOverlay.Insertion.Front, armParser, globalAddressMap);

            byte[] BattleLevelOvlData = romHandler.readOverlay(BattleLevelOvlNumber);
            int BattleLevelOvlAddress = romHandler.getOverlayAddress(BattleLevelOvlNumber);
            BattleLevelOvl = new ParagonLiteOverlay(romHandler, BattleLevelOvlNumber, "BattleLevel", BattleLevelOvlData, BattleLevelOvlAddress, ParagonLiteOverlay.Insertion.Restricted, armParser, globalAddressMap);

            byte[] battleServerOvlData = romHandler.readOverlay(battleServerOvlNumber);
            int battleServerOvlAddress = romHandler.getOverlayAddress(battleServerOvlNumber);
            battleServerOvl = new ParagonLiteOverlay(romHandler, battleServerOvlNumber, "BattleServer", battleServerOvlData, battleServerOvlAddress, ParagonLiteOverlay.Insertion.Restricted, armParser, globalAddressMap);

            byte[] trainerAIOvlData = romHandler.readOverlay(trainerAIOvlNumber);
            int trainerAIOvlAddress = romHandler.getOverlayAddress(trainerAIOvlNumber);
            trainerAIOvl = new ParagonLiteOverlay(romHandler, trainerAIOvlNumber, "TrainerAI", trainerAIOvlData, trainerAIOvlAddress, ParagonLiteOverlay.Insertion.Front, armParser, globalAddressMap);

            byte[] storageOvlData = romHandler.readOverlay(storageOvlNumber);
            int storageOvlAddress = romHandler.getOverlayAddress(storageOvlNumber);
            storageOvl = new ParagonLiteOverlay(romHandler, storageOvlNumber, "Storage", storageOvlData, storageOvlAddress, ParagonLiteOverlay.Insertion.Back, armParser, globalAddressMap);
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
//        decode(battleOvl, "ServerControl_MoveStatStageChangeEffectCommon");

        pokes = params.pokes;
        moves = params.moves;

        pokemonGraphicsNarc = params.pokemonGraphicsNarc;
        moveAnimationsNarc = params.moveAnimationsNarc;
        itemDataNarc = params.itemDataNarc;
        itemGraphicsNarc = params.itemGraphicsNarc;
        moveAnimationScriptsNarc = params.moveAnimationScriptsNarc;
        battleAnimationScriptsNarc = params.battleAnimationScriptsNarc;
        battleUIGraphicsNarc = params.battleUIGraphicsNarc;

        battleEventStrings1 = params.battleEventStrings1;
        battleEventStrings2 = params.battleEventStrings2;

        abilityNames = params.abilityNames;
        abilityDescriptions = params.abilityDescriptions;
        abilityExplanations = params.abilityExplanations;

        moveNames = params.moveNames;
        moveDescriptions = params.moveDescriptions;

        itemNames = params.itemNames;
        itemNameMessages = params.itemNameMessages;
        itemPluralNames = params.itemPluralNames;
        itemDescriptions = params.itemDescriptions;

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

    public void save() {
        System.out.print("Writing overlays");
        long startTime = System.currentTimeMillis();
        arm9.save(romHandler);
        battleOvl.save(romHandler);
        BattleLevelOvl.save(romHandler);
        battleServerOvl.save(romHandler);
        trainerAIOvl.save(romHandler);
        storageOvl.save(romHandler);
        System.out.printf(" - done, time=%dms\n", System.currentTimeMillis() - startTime);
    }

    private void decode(ParagonLiteOverlay overlay, String funcName) {
        ArmDecoder decoder = new ArmDecoder();

        int ramAddress = globalAddressMap.getRamAddress(overlay, funcName);
        int romAddress = overlay.ramToRomAddress(ramAddress);
        int size = armParser.getFuncSize(overlay, romAddress);

        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) overlay.readUnsignedByte(romAddress + i);
        }

        List<String> testLines = new ArrayList<>();
        try {
            testLines = decoder.decode(overlay, ramAddress, bytes, globalAddressMap);
        } catch (ArmDecodeException e) {
            throw new RuntimeException(e);
        }

        return;
    }

    public void setBattleEventStrings() {
        setBattleEventStrings1();
        setBattleEventStrings2();
    }

    private void setBattleEventStrings1() {
//        while (battleEventStrings1.size() < 221) battleEventStrings1.add("");
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

        // Frostbite
        battleEventStrings2.set(288, "\uF000Ă\\x0001\\x0000 got\\xFFFEfrostbite!");
        battleEventStrings2.set(289, "The wild \uF000Ă\\x0001\\x0000 got\\xFFFEfrostbite!");
        battleEventStrings2.set(290, "The foe's \uF000Ă\\x0001\\x0000 got\\xFFFEfrostbite!");
        battleEventStrings2.set(291, "\uF000Ă\\x0001\\x0000 was hurt\\xFFFEby its frostbite!");
        battleEventStrings2.set(292, "The wild \uF000Ă\\x0001\\x0000 was hurt\\xFFFEby its frostbite!");
        battleEventStrings2.set(293, "The foe's \uF000Ă\\x0001\\x0000 was hurt\\xFFFEby its frostbite!");
        battleEventStrings2.set(294, "\uF000Ă\\x0001\\x0000's frostbite was healed.");
        battleEventStrings2.set(295, "The wild \uF000Ă\\x0001\\x0000's frostbite was healed.");
        battleEventStrings2.set(296, "The foe's \uF000Ă\\x0001\\x0000's frostbite was healed.");
        battleEventStrings2.set(297, "\uF000Ă\\x0001\\x0000 already\\xFFFEhas frostbite!");
        battleEventStrings2.set(298, "The wild \uF000Ă\\x0001\\x0000 already\\xFFFEhas frostbite!");
        battleEventStrings2.set(299, "The foe's \uF000Ă\\x0001\\x0000 already\\xFFFEhas frostbite!");

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
        battleEventStrings2.add/* 1197 */("\uF000Ă\\x0001\\x0000 is veiled\\xFFFEin water!");
        battleEventStrings2.add/* 1198 */("The wild \uF000Ă\\x0001\\x0000 is veiled\\xFFFEin water!");
        battleEventStrings2.add/* 1199 */("The foe's \uF000Ă\\x0001\\x0000 is veiled\\xFFFEin water!");

        // Wind Power
        battleEventStrings2.add/* 1200 */("Being hit by \uF000ć\\x0001\\x0000 charged\\xFFFE\uF000Ă\\x0001\\x0001 with power!");
        battleEventStrings2.add/* 1201 */("Being hit by \uF000ć\\x0001\\x0000 charged\\xFFFEthe wild \uF000Ă\\x0001\\x0001 with power!");
        battleEventStrings2.add/* 1202 */("Being hit by \uF000ć\\x0001\\x0000 charged\\xFFFEthe foe's \uF000Ă\\x0001\\x0001 with power!");

        // Supreme Overloard
        battleEventStrings2.add/* 1203 */("\uF000Ă\\x0001\\x0000 gained strength\\xFFFEfrom the fallen!");
        battleEventStrings2.add/* 1204 */("The wild \uF000Ă\\x0001\\x0000 gained strength\\xFFFEfrom the fallen!");
        battleEventStrings2.add/* 1205 */("The foe's \uF000Ă\\x0001\\x0000 gained strength\\xFFFEfrom the fallen!");

        // Electro Shot
        battleEventStrings2.add/* 1206 */("\uF000Ă\\x0001\\x0000 absorbed\\xFFFEelectricity!");
        battleEventStrings2.add/* 1207 */("The wild \uF000Ă\\x0001\\x0000 absorbed\\xFFFEelectricity!");
        battleEventStrings2.add/* 1208 */("The foe's \uF000Ă\\x0001\\x0000 absorbed\\xFFFEelectricity!");

        // Meteor Beam
        battleEventStrings2.add/* 1209 */("\uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith space power!");
        battleEventStrings2.add/* 1210 */("The wild \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith space power!");
        battleEventStrings2.add/* 1211 */("The foe's \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith space power!");

        // Sticky Web
        battleEventStrings2.add/* 1212 */("\uF000Ă\\x0001\\x0000 was caught\\xFFFEin a sticky web!");
        battleEventStrings2.add/* 1213 */("The wild \uF000Ă\\x0001\\x0000 was caught\\xFFFEin a sticky web!");
        battleEventStrings2.add/* 1214 */("The foe's \uF000Ă\\x0001\\x0000 was caught\\xFFFEin a sticky web!");
        battleEventStrings1.add/* 1215 */("A sticky web has been laid out on the ground\\xFFFEaround your team!");
        battleEventStrings1.add/* 1216 */("A sticky web has been laid out on the ground\\xFFFEaround the foe's team!");
        battleEventStrings1.add/* 1217 */("The sticky web disappeared from\\xFFFEaround your team!");
        battleEventStrings1.add/* 1218 */("The sticky web disappeared from\\xFFFEaround the foe's team!");
    }

    public void tempFixFairyStruggle() {
        // Redux already has Fairy implementation but still needs this bug fixed

        int getMoveParamRamAddress = globalAddressMap.getRamAddress(battleOvl, "ServerEvent_GetMoveParam");
        int getMoveParamRomAddress = battleOvl.ramToRomAddress(getMoveParamRamAddress);

        battleOvl.writeByte(getMoveParamRomAddress + 0x9A, 0x12);
    }

    public void setPokeData() {
        // TODO: Because of PokeStar Studios, these are different between versions!

        // Updates the personal data to allow for abilities up to index 1023
        List<String> readPersonalDatalines = readLines("read_poke_personal_data.s");
        arm9.writeCodeForceInline(readPersonalDatalines, "ReadPokePersonalData");

        // Updates the box data to allow for abilities up to index 1023
        // Also fixes the Azurill->Marill gender bug
        List<String> readBoxDataLines = readLines("read_poke_box_data.s");
        arm9.writeCodeForceInline(readBoxDataLines, "ReadPokeBoxData");

        // Updates the box data to allow for abilities up to index 1023
        // Also fixes the Azurill->Marill gender bug
        List<String> writeBoxDataLines = readLines("write_poke_box_data.s");
        arm9.writeCodeForceInline(writeBoxDataLines, "WritePokeBoxData");

        System.out.println("Set Poke Data");
    }

    public void setBoxPreview() {
        // This is the function that creates the struct used for a Pokémon's preview in the PC.
        // We are essentially swapping the markings (which is given 2 bytes despite only using 1) and ability.
        List<String> makeBox2MainLines = readLines("storage/make_box2_main.s");
        storageOvl.writeCodeForceInline(makeBox2MainLines, "MakeBox2Main");

        // This function calls PreviewCore as well as gets the Box2Main's markings, which have been moved, so we adjust
        List<String> displayPreviewLines = readLines("storage/display_preview.s");
        storageOvl.writeCodeForceInline(displayPreviewLines, "DisplayPreview");

        // Update to use the correct ability string when filling out the display field for ability
        List<String> previewAbilityLines = readLines("storage/preview_ability.s");
        storageOvl.writeCodeForceInline(previewAbilityLines, "Preview_Ability");
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

    public void setGetEffectiveWeather() {
        // Allows for Damp and Sun-Soaked to override the current weather for outgoing moves

        String getEffectiveWeatherFilename;
        switch (mode) {
            case ParagonLite -> getEffectiveWeatherFilename = "battle/get_effective_weather.s";
            case Redux -> getEffectiveWeatherFilename = "battle/get_effective_weather_redux.s";
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
        List<String> getEffectiveWeatherLines = readLines(getEffectiveWeatherFilename);
        battleOvl.writeCode(getEffectiveWeatherLines, "GetEffectiveWeather");

        List<String> getHandlerEffectiveWeatherLines = readLines("battle/handler_get_effective_weather.s");
        battleOvl.writeCode(getHandlerEffectiveWeatherLines, "Handler_GetEffectiveWeather");

        System.out.println("Set GetEffectiveWeather");
    }

    public void setCalcDamageOffensiveValue() {
        // Allows for stat to be modified (for Body Press)
        List<String> lines = readLines("calc_damage_get_offensive_value.s");
        battleOvl.replaceCode(lines, "ServerEvent_GetOffensiveValue");
        System.out.println("Set damage calc defensive stat");
    }

    public void setCalcDamageDefensiveValue() {
        // Grants Ice-type Pokémon a 1.5x Defense boost in hail
        // Makes Fighting-type Pokémon immune to hail damage
        List<String> lines = readLines("calc_damage_get_defensive_value.s");
        battleOvl.replaceCode(lines, "ServerEvent_GetDefensiveValue");
        System.out.println("Set damage calc defensive stat");
    }

    public void setCalcDamage() {
        // Modernizes critical hit damage
        // 2.0x -> 1.5x
        // Frostbite 0.5x damage for Special moves
        // Facade ignores

        List<String> lines = readLines("calc_damage.s");
        battleOvl.writeCodeForceInline(lines, "ServerEvent_CalcDamage");
        System.out.println("Set damage calc");
    }

    public void setCritRatio() {
        List<String> lines = readLines("check_critical_hit.s");
        battleOvl.writeCodeForceInline(lines, "CheckCriticalHit");

        byte[] critChanceData = new byte[]{24, 8, 2, 1};
        battleOvl.writeData(critChanceData, "Data_CriticalHitChances");

        System.out.println("Set critical hit ratios");
    }

    public void setStatus() {
        // Modernizes Burn damage
        // 1/8 -> 1/16
        //
        // Sets Frostbite damage
        // 1/16

        List<String> conditionDamageRecallLines = readLines("condition_damage_recall.s");
        battleOvl.writeCodeForceInline(conditionDamageRecallLines, "ConditionDamageRecall");

        List<String> statusDamageLines = readLines("get_status_damage.s");
        battleOvl.writeCodeForceInline(statusDamageLines, "GetStatusDamage");

        List<String> statusDamageStringLines = readLines("get_status_damage_string.s");
        battleServerOvl.writeCodeForceInline(statusDamageStringLines, "Condition_GetDamageText");

        List<String> effectMainUpdateConditionLines = readLines("effect_main_update_condition.s");
        BattleLevelOvl.writeCodeForceInline(effectMainUpdateConditionLines, "EffectMain_UpdateCondition");

        // Replaces Freeze with Frostbite

//        int burnColorEffect = BattleLevelOvl.readUnsignedHalfword(0x021E6A80 + 2 * 5);
//        BattleLevelOvl.writeHalfword(0x021E6A80 + 2 * 2, burnColorEffect);

        // Skips the move fail check for being frozen
        int moveExeCheck1FreezeRamAddress = globalAddressMap.getRamAddress(battleOvl, "ServerControl_MoveExeCheck1");
        int moveExeCheck1FreezeRomAddress = battleOvl.ramToRomAddress(moveExeCheck1FreezeRamAddress);
        battleOvl.writeHalfword(moveExeCheck1FreezeRomAddress + 0x3E, 0xE009);

        // skips the 20% chance of recovering from freeze
        int checkMoveExeFreezeThawRamAddress = globalAddressMap.getRamAddress(battleOvl, "ServerControl_CheckMoveExeFreezeThaw");
        int checkMoveExeFreezeThawRomAddress = battleOvl.ramToRomAddress(checkMoveExeFreezeThawRamAddress);
        battleOvl.writeHalfword(checkMoveExeFreezeThawRomAddress + 0x24, 0xD007);

        // being hit by a fire move will thaw the user out, but this isn't the case for frostbite
        int damageFreezeThawRamAddress = globalAddressMap.getRamAddress(battleOvl, "ServerControl_DamageFreezeThaw");
        int damageFreezeThawRomAddress = battleOvl.ramToRomAddress(damageFreezeThawRamAddress);
        battleOvl.writeHalfword(damageFreezeThawRomAddress, 0x4770); // Immediately exits the function with "bx lr"

        setBattleAnimation(601, "frostbite");

        BitmapFile.GraphicsFileParams conditionBadgesSpriteParams = new BitmapFile.GraphicsFileParams();
        conditionBadgesSpriteParams.subImageCount = 8;

        setUISprite(12, "condition_badges.bmp", conditionBadgesSpriteParams);

        // Paralysis Speed 25% -> 50%
        int calculateSpeedRamAddress = globalAddressMap.getRamAddress(battleOvl, "ServerEvent_CalculateSpeed");
        int calculateSpeedRomAddress = battleOvl.ramToRomAddress(calculateSpeedRamAddress);
        battleOvl.writeByte(calculateSpeedRomAddress + 0x80, 50);
    }

    public void setStatChangeIntimidateFlag() {
        // Updates the stat change functions to include a flag for a stat change being caused by intimidate

        List<String> checkStatChangeSuccessLines = readLines("battle/server_event_check_stat_change_success.s");
        battleOvl.writeCodeForceInline(checkStatChangeSuccessLines, "ServerEvent_CheckStatChangeSuccess");

        List<String> statStageChangeAppliedLines = readLines("battle/server_event_stat_stage_change_applied.s");
        battleOvl.writeCodeForceInline(statStageChangeAppliedLines, "ServerEvent_StatStageChangeApplied");

        List<String> statStageChangeFailedLines = readLines("battle/server_event_stat_stage_change_failed.s");
        battleOvl.writeCodeForceInline(statStageChangeFailedLines, "ServerEvent_StatStageChangeFailed");

        List<String> statStageChangeCoreLines = readLines("battle/server_control_stat_stage_change_core.s");
        battleOvl.writeCodeForceInline(statStageChangeCoreLines, "ServerControl_StatStageChangeCore");

        List<String> handlerChangeStatStageLines = readLines("battle/handler_change_stat_stage.s");
        battleOvl.writeCodeForceInline(handlerChangeStatStageLines, "Handler_ChangeStatStage");

        List<String> moveStatStageChangeEffectCommonLines = readLines("battle/server_control_move_stat_stage_change_effect_common.s");
        battleOvl.writeCodeForceInline(moveStatStageChangeEffectCommonLines, "ServerControl_MoveStatStageChangeEffectCommon");
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
        if (mode != Mode.ParagonLite)
            return;

        // Grants Fighting-type Pokémon immunity to hail
        List<String> lines = readLines("is_poke_damaged_by_weather.s");
        battleOvl.replaceCode(lines, "IsPokeDamagedByWeather");

        System.out.println("Set weather damage");
    }

    public void setShinyRate() {
        if (mode != Mode.ParagonLite)
            return;

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

    public void setHandlerSimulationDamage() {
        List<String> multiStrikeMultiplierLines = readLines("handler_get_simulation_multi-strike_multiplier.s");
        battleOvl.writeCode(multiStrikeMultiplierLines, "Handler_GetSimulationMultiStrikeMultiplier");

        // Updates simulation damage to include move bindings for variable power, type, effectiveness, etc.
        List<String> simulationDamageLines = readLines("handler_simulation_damage.s");
        battleOvl.writeCodeForceInline(simulationDamageLines, "Handler_SimulationDamage");

        System.out.println("Set Handler Simulation Damage");
    }

    public void setScreenPower() {
        // Updates Light Screen and Reflect to use the proper 33% reduction seen in Gen VI onwards for double/triple battles 
        int ramAddress = globalAddressMap.getRamAddress(battleServerOvl, "CommonScreenEffect");
        int romAddress = battleServerOvl.ramToRomAddress(ramAddress);
        battleServerOvl.writeWord(romAddress + 0x4C, 2732, false);

        System.out.println("Set screen power");
    }

    public void setNewSideStatus() {
        int sideEffectEventAddItemRamAddress = globalAddressMap.getRamAddress(battleServerOvl, "SideStatus_AddItem");
        int sideEffectEventAddItemRomAddress = battleServerOvl.ramToRomAddress(sideEffectEventAddItemRamAddress);

        int sideStatusCountByteAddress = sideEffectEventAddItemRomAddress + 0x92;
        int sideStatusAddTableRef = sideEffectEventAddItemRomAddress + 0xA8;
        int sideStatusAddTable4Ref = sideEffectEventAddItemRomAddress + 0xAC;

        int newCount = 15; // Sticky Web, Aurora Veil
        battleServerOvl.writeByte(sideStatusCountByteAddress, newCount);

        int battleSideStatusInitRamAddress = globalAddressMap.getRamAddress(battleServerOvl, "SideStatus_Init");
        int battleSideStatusInitRomAddress = battleServerOvl.ramToRomAddress(battleSideStatusInitRamAddress);
        battleServerOvl.writeByte(battleSideStatusInitRomAddress, newCount); // mov r2, #newCount
        battleServerOvl.writeHalfword(battleSideStatusInitRomAddress + 0x08, 0x0152); // lsl r1, #5

        updateBattleServerFunctionSideStatusCount("SideStatus_AddItem", 0x0C, newCount);
        updateBattleServerFunctionSideStatusCount("SideStatus_GetCount", 0x00, newCount);
        updateBattleServerFunctionSideStatusCount("SideStatus_IsEffectActive", 0x00, newCount);
        updateBattleServerFunctionSideStatusCount("SideStatus_RemoveItem", 0x02, newCount);
        updateBattleServerFunctionSideStatusCount("SideStatus_TurnCheck", 0x0E, newCount);
        updateBattleServerFunctionSideStatusCount("SideStatus_GetCountFromEventItem", 0x08, newCount);

        int oldSideStatusAddTable = battleServerOvl.readWord(sideStatusAddTableRef);

        byte[] newData = new byte[12 * newCount];

        for (int i = 0; i < 14; ++i) {
            int sideStatusId = battleServerOvl.readWord(oldSideStatusAddTable + i * 12);
            int sideStatusEffectRef = battleServerOvl.readWord(oldSideStatusAddTable + i * 12 + 4);
            int sideStatusMaxLevel = battleServerOvl.readWord(oldSideStatusAddTable + i * 12 + 8);

            writeWord(newData, i * 12, sideStatusId);
            writeWord(newData, i * 12 + 4, sideStatusEffectRef);
            writeWord(newData, i * 12 + 8, sideStatusMaxLevel);
        }

        addSideStatus(newData, 14, "StickyWeb", "sticky_web", Gen5BattleEventType.onSwitchIn);
//        addSideStatus(newData, 15, "AuroraVeil", "aurora_veil", Gen5BattleEventType.onMoveDamageProcessing2);

        int effectTableRomAddress = battleOvl.writeData(newData, "Data_SideStatusEffectTable");
        int effectTableRamAddress = battleOvl.romToRamAddress(effectTableRomAddress);
        battleServerOvl.writeWord(sideStatusAddTableRef, effectTableRamAddress, true);
        battleServerOvl.writeWord(sideStatusAddTable4Ref, effectTableRamAddress + 4, true);
    }

    private void updateBattleServerFunctionSideStatusCount(String functionName, int instructionOffset, int newSideStatusCount) {
        int funcRamAddress = globalAddressMap.getRamAddress(battleServerOvl, functionName);
        int funcRomAddress = battleServerOvl.ramToRomAddress(funcRamAddress);

        battleServerOvl.writeByte(funcRomAddress + instructionOffset, newSideStatusCount * 16);
    }

    private void addSideStatus(byte[] data, int statusId, String name, String handlerName, int eventType) {
        List<String> handlerLines = readLines(String.format("battleserver/handler_side_%s.s", handlerName));
        int handlerLinesRomAddress = battleOvl.writeCode(handlerLines, String.format("HandlerSide_%s", name));
        int handlerLinesRamAddress = battleOvl.romToRamAddress(handlerLinesRomAddress);

        byte[] eventTable = new byte[8];
        writeWord(eventTable, 0, eventType);
        writeWord(eventTable, 4, handlerLinesRamAddress + 1);
        battleOvl.writeData(eventTable, String.format("HandlerSide_%s_EventTable", name));

        List<String> eventAddLines = readLines(String.format("battleserver/side_status_event_add_%s.s", handlerName));
        int eventAddRomAddress = battleOvl.writeCode(eventAddLines, String.format("SideStatusEventAdd_%s", name));
        int eventAddRamAddress = battleOvl.romToRamAddress(eventAddRomAddress);

        writeWord(data, statusId * 12, statusId);
        writeWord(data, statusId * 12 + 4, eventAddRamAddress + 1);
        writeWord(data, statusId * 12 + 8, 1); // max level
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

    public void setTrainerAI() {
//        // TODO: This is used for the multiplicative version of move selection
//        trainerAIOvl.writeByte(0x0217F842, 4096);

//        writeTrainerAIFile(trainerAIScriptsNarc, 14); // Test

        int battleClientLineNumRamAddress = globalAddressMap.getRamAddress(battleOvl, "Data_BattleClient_Init_LineNum");
        int battleClientLineNumRomAddress = battleOvl.ramToRomAddress(battleClientLineNumRamAddress);
        int battleClientLineNum = battleOvl.readWord(battleClientLineNumRomAddress);

        int battleClientAllocModifyRamAddress = globalAddressMap.getRamAddress(battleOvl, "Inst_BattleClient_Init_AllocModify");
        int battleClientAllocModifyRomAddress = battleOvl.ramToRomAddress(battleClientAllocModifyRamAddress);
        int oldBattleClientAllocDiff = -battleOvl.readUnsignedByte(battleClientAllocModifyRomAddress);

        int threatVarSize = 2;
        int numPartiesPerSide = 2;
        int numPokesPerParty = 6;
        int flagsSize = 4;

        // 0x02A0
        int threatArrSize = (threatVarSize * numPokesPerParty);
        int memPokeSize = threatArrSize + flagsSize;

        int oldBattleClientSize = battleClientLineNum + oldBattleClientAllocDiff;
        int battleClientMemBlockSize = numPartiesPerSide * numPokesPerParty * memPokeSize;
        int newBattleClientSize = oldBattleClientSize + battleClientMemBlockSize;

//        if (Math.abs(newBattleClientSize) > 512)
//            throw new RuntimeException("Allocation too large");

        // lsl r1, r2, #9 (512)
//        battleOvl.writeHalfword(battleClientAllocModifyRomAddress, (9 << 6) | 0x0011);

        if (oldBattleClientSize != 596)
            throw new RuntimeException("This needs to be updated");

        armParser.setStructFieldOffset("BtlClientWk", "aiMemPokeArr", oldBattleClientSize);

        String aiMemStructName = "AIMemPoke";
        armParser.addStruct(aiMemStructName);
        armParser.setStructFieldOffset(aiMemStructName, "size", memPokeSize);
        armParser.setStructFieldOffset(aiMemStructName, "threatsArr", 0x00);
        armParser.setStructFieldOffset(aiMemStructName, "flags", threatArrSize);

        armParser.addGlobalValue("AIMEM_Revealed", 0x00);
        armParser.addGlobalValue("AIMEM_Ability", 0x01);
        armParser.addGlobalValue("AIMEM_Item", 0x02);
        armParser.addGlobalValue("AIMEM_HPType", 0x03);
        armParser.addGlobalValue("AIMEM_Move1", 0x04);
        armParser.addGlobalValue("AIMEM_Move2", 0x05);
        armParser.addGlobalValue("AIMEM_Move3", 0x06);
        armParser.addGlobalValue("AIMEM_Move4", 0x07);
        armParser.addGlobalValue("AIMEM_Stat_HP", 0x08);
        armParser.addGlobalValue("AIMEM_Stat_Attack", 0x09);
        armParser.addGlobalValue("AIMEM_Stat_Defense", 0x0A);
        armParser.addGlobalValue("AIMEM_Stat_Speed", 0x0B);
        armParser.addGlobalValue("AIMEM_Stat_SpAtk", 0x0C);
        armParser.addGlobalValue("AIMEM_Stat_SpDef", 0x0D);

        armParser.setStructFieldOffset("BtlClientWk", "size", newBattleClientSize);

        int scriptCommandTableRamAddress = globalAddressMap.getRamAddress(trainerAIOvl, "Data_AIScriptCommands");
        int scriptCommandTableRomAddress = trainerAIOvl.ramToRomAddress(scriptCommandTableRamAddress);

        // New OP: Multiply Score (0x2B)
        List<String> multiplyScoreLines = readLines("trainerai/scripts/multiply_score.s");
        int multiplyScoreAddress = trainerAIOvl.writeCode(multiplyScoreLines, "MultiplyScore");
        trainerAIOvl.writeWord(scriptCommandTableRomAddress + (0x2B * 4), multiplyScoreAddress + 1, true);

        // New OP: Multiply Score by Stored (0x3C)
        List<String> multiplyScoreByStoredLines = readLines("trainerai/scripts/multiply_score_by_stored.s");
        int multiplyScoreByStoredAddress = trainerAIOvl.writeCode(multiplyScoreByStoredLines, "MultiplyScoreByStored");
        trainerAIOvl.writeWord(scriptCommandTableRomAddress + (0x3C * 4), multiplyScoreByStoredAddress + 1, true);

        // New OP: Get Simulation Multiplier (0x3E)
        List<String> getSimulationMultiplierLines = readLines("trainerai/scripts/get_simulation_multiplier.s");
        int getDamageAddress = trainerAIOvl.writeCode(getSimulationMultiplierLines, "GetSimulationMultiplier");
        trainerAIOvl.writeWord(scriptCommandTableRomAddress + (0x3E * 4), getDamageAddress + 1, true);

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

        int totalChanges = 90;
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

        // TODO: #508 Rip Tide

        // #509 Wind Whipper
        Utils.printProgress(totalChanges, ++currentChanges, "Wind Whipper");
        addWindWhipper();

        // #510 Glazeware
        Utils.printProgress(totalChanges, ++currentChanges, "Glazeware");
        addGlazeware();

        // #511 Sun-Soaked
        Utils.printProgress(totalChanges, ++currentChanges, "Sun-Soaked");
        addSunSoaked();

        // #512 Colossal
        Utils.printProgress(totalChanges, ++currentChanges, "Colossal");
        addColossal();

        // #513 Final Thread
        Utils.printProgress(totalChanges, ++currentChanges, "Final Thread");
        addFinalThread();

        // #514 Home Grown
        Utils.printProgress(totalChanges, ++currentChanges, "Home Grown");
        addHomeGrown();

        // #515 Ravenous Torque
        Utils.printProgress(totalChanges, ++currentChanges, "Ravenous Torque");
        addRavenousTorque();

        // #516 Superconductor
        Utils.printProgress(totalChanges, ++currentChanges, "Superconductor");
        addSuperconductor();


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

        // #012 Oblivious (+ Taunt immunity, + Intimidate immunity)
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

        // #020 Own Tempo (+ Intimidate immunity)
        Utils.printProgress(totalChanges, ++currentChanges, "Own Tempo");
        setOwnTempo();

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

        // #039 Inner Focus (+ Intimidate Immunity)
        Utils.printProgress(totalChanges, ++currentChanges, "Inner Focus");
        setInnerFocus();

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
        
        // #077 Tangled Feet (Boost Speed on miss)
        Utils.printProgress(totalChanges, ++currentChanges, "Tangled Feet");
        setTangledFeet();

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

        // #113 Scrappy
        Utils.printProgress(totalChanges, ++currentChanges, "Scrappy");
        setScrappy();

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

        // #155 Rattled (+ Activate on Intimidate)
        Utils.printProgress(totalChanges, ++currentChanges, "Rattled");
        setRattled();

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
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onDecideTarget, "protean.s"));
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
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "fur_coat_redux.s"));
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
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "bulletproof.s"));
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
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeSuccess, "competitive"));
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
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveParam, "refrigerate_type"),
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
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onGetMovePriority, "gale_wings"));
    }

    private void addMegaLauncher() {
        // TODO: Make this work for Heal Pulse

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
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveParam, "pixilate_type"),
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "common_move_type_change_power"));
    }

    private void addGooey() {
        int number = Abilities.gooey;

        // Name
        abilityNames.set(number, "Gooey");

        // Description
        abilityDescriptions.set(number, "Contact moves lower the\\xFFFEattacker's Speed stat.");

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "gooey.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveParam, "aerilate_type"),
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
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "stamina"));
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
                new AbilityEventHandler(Gen5BattleEventType.onCalcSpeed, "slush_rush_speed.s"),
                new AbilityEventHandler(Gen5BattleEventType.onWeatherReaction, "slush_rush_weather_immune.s"));
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
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePriority, "triage"));
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
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveParam, "galvanize_type.s"),
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
            case Redux -> filename = "fluffy_redux.s";
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, filename));
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
                new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "wind_rider_immunity.s"),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "wind_rider_on_enter.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "wind_rider_on_enter.s"),
                new AbilityEventHandler(Gen5BattleEventType.OnMoveExecuteEffective, "wind_rider_after_tailwind.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "supreme_overlord_on_enter.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "supreme_overlord_on_enter.s"),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchOut, "supreme_overlord_on_exit.s"));
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
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onApplySTAB, Abilities.adaptability));
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
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "insectivore.s"));
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
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onDamageProcessingEnd_HitReal, "prestige"));
    }

    private void addLuckyFoot() {
        int index = ParagonLiteAbilities.luckyFoot;

        // Name
        switch (mode) {
            case ParagonLite -> abilityNames.set(index, "Lucky Foot");
            case Redux -> abilityNames.set(index, "Steel Toecap");
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

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
        setAbilityEventHandlers(index, new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "assimilate"));
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
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "stone_home_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "stone_home_message"));
    }

    private void addCacophony() {
        int number = ParagonLiteAbilities.cacophony;

        // Name
        abilityNames.set(number, "Cacophony");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist).replace("punching", "sound");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Cacophony")
                    .replace("moves that punch", "sound-based moves");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "cacophony_boost.s"),
                new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "cacophony_immunity.s"));
    }

    private void addWindWhipper() {
        int number = ParagonLiteAbilities.windWhipper;

        // Name
        abilityNames.set(number, "Wind Whipper");

        // Description
        String description = abilityDescriptions.get(Abilities.ironFist).replace("punching", "wind");
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.ironFist)
                    .replace("Iron Fist", "Wind Whipper")
                    .replace("moves that punch", "wind-based moves");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "wind_whipper.s"));
    }

    private void addGlazeware() {
        int number = ParagonLiteAbilities.glazeware;

        // Name
        abilityNames.set(number, "Glazeware");

        // Description
        String description = "Resists Water- and\\xFFFEPoison-type moves.";
        abilityDescriptions.set(number, description);

        // Explanation
        if (abilityExplanations != null) {
            String explanation = abilityExplanations.get(Abilities.thickFat)
                    .replace("Fire", "Water")
                    .replace("Ice", "Poison");
            abilityExplanations.set(number, explanation);
        }

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "glazeware.s"));
    }

    private void addSunSoaked() {
        int number = ParagonLiteAbilities.sunSoaked;

        // Name
        abilityNames.set(number, "Sun-Soaked");

        // Description
        String description = "All moves get the effects\\xFFFEof harsh sunlight.";
        abilityDescriptions.set(number, description);

        // TODO: Explanation

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "sun-soaked_fire_resistance.s"));
    }

    private void addColossal() {
        int number = ParagonLiteAbilities.colossal;

        // Name
        abilityNames.set(number, "Colossal");

        // Description
        String description = "Reduces damage from attacks,\\xFFFEbut also reduces evasion.";
        abilityDescriptions.set(number, description);

        // TODO: Explanation

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, "colossal_damage.s"),
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveAccuracy, "colossal_accuracy.s"));
    }

    private void addFinalThread() {
        int number = ParagonLiteAbilities.finalThread;

        // Name
        abilityNames.set(number, "Final Thread");

        // Description
        String description = "Sets Sticky Web and\\xFFFEToxic Spikes on faint.";
        abilityDescriptions.set(number, description);

        // TODO: Explanation

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "final_thread.s"));
    }

    private void addHomeGrown() {
        // TODO
    }

    private void addRavenousTorque() {
        // TODO
    }

    private void addSuperconductor() {
        int number = ParagonLiteAbilities.superconductor;

        // Name
        switch (mode) {
            case ParagonLite -> abilityNames.set(number, "Superconductor");
            case Redux -> abilityNames.set(number, "Coolant Boost");
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

        // Description
        String description = abilityDescriptions.get(Abilities.flareBoost).replace("burned", "frostbitten");
        abilityDescriptions.set(number, description);
        
        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "superconductor.s"));
    }

    private void setStench() {
        if (mode == Mode.Redux) {
            int number = Abilities.stench;
            setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMoveFlinchChance, "stench_redux.s"));
        }
    }

    private void setDamp() {
        int number = Abilities.damp;

        switch (mode) {
            case ParagonLite -> {
                // TODO Description
                String description = "All moves get the\\xFFFEeffects of rain.";
                abilityDescriptions.set(number, description);

                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, Abilities.heatproof));
            }
            case Redux -> {
                setAbilityEventHandlers(number,
                        // Old Damp
                        new AbilityEventHandler(Gen5BattleEventType.onMoveExecuteCheck2),
                        new AbilityEventHandler(Gen5BattleEventType.onMoveExecuteFail),
                        new AbilityEventHandler(Gen5BattleEventType.onMoveSequenceStart),
                        new AbilityEventHandler(Gen5BattleEventType.onMoveSequenceEnd),
                        new AbilityEventHandler(Gen5BattleEventType.onAbilityNullified),

                        // Old Heatproof
                        new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, Abilities.heatproof),
                        new AbilityEventHandler(Gen5BattleEventType.onConditionDamage, Abilities.heatproof));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setLimber() {
        int number = Abilities.limber;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionCheckFail),
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionFail),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn),
                new AbilityEventHandler(Gen5BattleEventType.onActionProcessingEnd),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeLastCheck, "limber_speed.s"),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeFail, "limber_speed_message.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionCheckFail),
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionFail),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "oblivious_taunt.s"),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeLastCheck, "common_intimidate_immunity.s"),
                new AbilityEventHandler(Gen5BattleEventType.onActionProcessingEnd));
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
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onDecideTarget, "protean.s"));
    }

    private void setImmunity() {
        int number = Abilities.immunity;
        abilityUpdates.put(number, "Prevents the poison status condition; NEW: Unaffected to Poison-type moves");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionCheckFail),
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionFail),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn),
                new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "immunity.s"),
                new AbilityEventHandler(Gen5BattleEventType.onActionProcessingEnd));
    }

    private void setOwnTempo() {
        int number = Abilities.ownTempo;
        abilityUpdates.put(number, "Prevents confusion; NEW: Immunity to Intimidate");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionCheckFail),
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionFail),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onActionProcessingEnd),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeLastCheck, "common_intimidate_immunity.s"));
    }

    private void setShadowTag() {
        int number = Abilities.shadowTag;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreventRun),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "shadow_tag_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "shadow_tag_message"));
    }

    private void setWonderGuard() {
        int number = Abilities.wonderGuard;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "wonder_guard_message.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "wonder_guard_message.s"));
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
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "illuminate_redux.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "huge_power_message.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "huge_power_message.s"));
    }

    private void setInnerFocus() {
        int number = Abilities.innerFocus;
        abilityUpdates.put(number, "Prevents flinch; NEW: Immunity to Intimidate");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onFlinchCheck),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeLastCheck, "common_intimidate_immunity.s"));
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
                        new AbilityEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "magma_armor_burn.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "magma_armor_resist.s"));
            }
            case Redux -> setAbilityEventHandlers(number,
                    new AbilityEventHandler(Gen5BattleEventType.onAddConditionCheckFail),
                    new AbilityEventHandler(Gen5BattleEventType.onAddConditionFail),
                    new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                    new AbilityEventHandler(Gen5BattleEventType.onSwitchIn),
                    new AbilityEventHandler(Gen5BattleEventType.onActionProcessingEnd),
                    new AbilityEventHandler(Gen5BattleEventType.onGetIsCriticalHit, Abilities.battleArmor));
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setWaterVeil() {
        int number = Abilities.waterVeil;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionCheckFail),
                new AbilityEventHandler(Gen5BattleEventType.onAddConditionFail),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange), // TODO Add message
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn), // TODO Add message
                new AbilityEventHandler(Gen5BattleEventType.onActionProcessingEnd),
                new AbilityEventHandler(Gen5BattleEventType.onWeatherReaction, Abilities.overcoat));
    }

    private void setMagnetPull() {
        int number = Abilities.magnetPull;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreventRun),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "magnet_pull_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "magnet_pull_message"));
    }

    private void setSandStream() {
        int number = Abilities.sandStream;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onWeatherReaction, "sand_stream_no_damage"));
    }

    private void setHyperCutter() {
        int number = Abilities.hyperCutter;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeLastCheck, "hyper_cutter.s"),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeFail));
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
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePriority, "hustle.s"));
                break;
            }
            case Redux: {
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePriority, "hustle_redux.s"));
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
                        new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "plus_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "plus_message.s"));
            }
            case Redux -> {
                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "plus_spatk_redux.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "plus_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "plus_message.s"));
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
                        new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "minus_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "minus_message.s"));
            }
            case Redux -> {
                // Description
                abilityDescriptions.set(number, "Boosts the Attack stat\\xFFFEof allies.");

                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "minus_attack_redux.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "minus_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "minus_message.s"));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setShedSkin() {
        int number = Abilities.shedSkin;

        // Description
        abilityDescriptions.set(number, "The Pokémon heals its\\xFFFEown status problems.");

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onCheckSpecialPriority, "shed_skin.s"));
    }

    private void setArenaTrap() {
        int number = Abilities.arenaTrap;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreventRun),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "arena_trap_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "arena_trap_message"));
    }

    private void setVitalSpirit() {
        int number = Abilities.vitalSpirit;

        // Description
        abilityDescriptions.set(number, "Boosts the Sp. Def stat\\xFFFEwhen hit by an attack.");

        // TODO: Explanation

        // Data
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "vital_spirit.s"));
    }

    private void setWhiteSmoke() {
        int number = Abilities.whiteSmoke;

        // Description
        abilityDescriptions.set(number, "Prevents the Pokémon's\\xFFFEstats from dropping.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeLastCheck, "white_smoke.s"),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeFail));
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
                        new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "pure_power_message.s"),
                        new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "pure_power_message.s"));
            }
            case Redux -> {
                // Explanation
                if (abilityExplanations != null) {
                    String explanation = abilityExplanations.get(number).replace("Attack", "Sp. Atk");
                    abilityExplanations.set(number, explanation);
                }

                // Data
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "pure_power_redux.s"));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }
    
    private void setTangledFeet() {
        int number = Abilities.tangledFeet;

        // Description
        abilityDescriptions.set(number, "Boosts Speed when moves\\xFFFEfail or miss.");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onMoveExecuteFail, "tangled_feet_flinch.s"),
                new AbilityEventHandler(Gen5BattleEventType.OnMoveExecuteNoEffect, "tangled_feet_miss.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "anger_point_crit"),
                new AbilityEventHandler(Gen5BattleEventType.onMoveExecuteFail, "anger_point_flinch"),
                new AbilityEventHandler(Gen5BattleEventType.OnMoveExecuteNoEffect, "anger_point_miss"));
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
                new AbilityEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, "adaptability.s"));
    }

    private void setSolarPower() {
        int number = Abilities.solarPower;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onWeatherReaction, "solar_power_weather.s"),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue, "solar_power_spatk_boost.s"));
    }

    private void setTechnician() {
        if (mode == Mode.Redux)
            return;

        // TODO: Reassess later
//        int number = Abilities.technician;
//        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onGetMovePower, "technician.s"));
    }

    private void setLeafGuard() {
        int number = Abilities.leafGuard;

        switch (mode) {
            case ParagonLite -> {
                // Description
                String description = "Reduces damage in\\xFFFEsunny weather.";
                abilityDescriptions.set(number, description);

                // Data
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, "leaf_guard.s"));
            }
            case Redux -> {
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onAddConditionCheckFail),
                        new AbilityEventHandler(Gen5BattleEventType.onAddConditionFail),
                        new AbilityEventHandler(Gen5BattleEventType.onCheckSleep),
                        new AbilityEventHandler(Gen5BattleEventType.onGetIsCriticalHit, Abilities.battleArmor));
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void setSuperLuck() {
        int number = Abilities.superLuck;

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetIsCriticalHit),
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn, "super_luck_message.s"),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange, "super_luck_message.s"));
    }

    private void setSlowStart() {
        int number = Abilities.slowStart;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn),
                new AbilityEventHandler(Gen5BattleEventType.onCheckActivation),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onCalcSpeed),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue),
                new AbilityEventHandler(Gen5BattleEventType.onTurnCheckEnd, "slow_start_end_of_turn.s"));
    }

    private void setScrappy() {
        int number = Abilities.scrappy;
        abilityUpdates.put(number, "Hit Ghost with Normal and Fighting; NEW: Immunity to Intimidate");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetEffectiveness),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeLastCheck, "common_intimidate_immunity.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onWeatherReaction),
                new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "ice_body_immunity.s"));
    }

    private void setSnowWarning() {
        int number = Abilities.snowWarning;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onWeatherReaction, "snow_warning_no_damage.s"));
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

        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onTurnCheckBegin, "healer_recover_hp.s"));
    }

    private void setFriendGuard() {
        int number = Abilities.friendGuard;

        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, "friend_guard.s"));
    }

    private void setWeakArmor() {
        int number = Abilities.weakArmor;

        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "weak_armor.s"));
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
                        new AbilityEventHandler(Gen5BattleEventType.onCalcSpeed, "heavy_metal_speed"));
            }
            case Redux -> {
                abilityDescriptions.set(number, abilityDescriptions.get(Abilities.filter));

                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onGetWeight),
                        new AbilityEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, Abilities.filter));
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
                new AbilityEventHandler(Gen5BattleEventType.onCalcSpeed, "light_metal_speed"));
    }

    private void setOvercoat() {
        int number = Abilities.overcoat;

        // Description
        String description = "Protects the Pokémon from\\xFFFEsand, hail and powder.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onWeatherReaction),
                new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "overcoat_powder_immunity"));
    }

    private void setRegenerator() {
        int number = Abilities.regenerator;

        if (mode == Mode.Redux) {
            // Data
            setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onSwitchOutEnd, "regenerator_redux.s"));
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
        setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onAbilityCheckNoEffect, "justified"));
    }

    private void setRattled() {
        int number = Abilities.rattled;
        abilityUpdates.put(number, "Prevents confusion; NEW: Immunity to Intimidate");

        // Data
        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onMoveDamageReaction1),
                new AbilityEventHandler(Gen5BattleEventType.onStatStageChangeSuccess, "rattled_on_intimidate.s"));
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
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onMoveSequenceStart, "turboblaze"),
                new AbilityEventHandler(Gen5BattleEventType.onMoveSequenceEnd),
                new AbilityEventHandler(Gen5BattleEventType.onAbilityNullified));
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

//         HACK: We have to allocate to Battle overlay and jump to that because we can't allocate new space in BattleLevel
        List<String> btlvEffVMLoadScriptLines = readLines("battlelevel/btlveffvm_load_script.s");
        battleOvl.writeCode(btlvEffVMLoadScriptLines, "BtlvEffVM_LoadScript");

        List<String> btlvEffVMLoadScriptJumpLines = readLines("battlelevel/btlveffvm_load_script_jump.s");
        BattleLevelOvl.writeCodeForceInline(btlvEffVMLoadScriptJumpLines, "BtlvEffVM_LoadScript");

        List<String> playMoveAnimationLines = readLines("battlelevel/play_move_animation.s");
        battleOvl.writeCode(playMoveAnimationLines, "PlayMoveAnimation");

        List<String> playMoveAnimationJumpLines = readLines("battlelevel/play_move_animation_jump.s");
        BattleLevelOvl.writeCodeForceInline(playMoveAnimationJumpLines, "PlayMoveAnimation");

        int maxMoveIndex = Moves.malignantChain;
        int highMoveOffset = 116;
        int battleAnimationScriptsOffset = 561;

        int maxMoveScriptIndex = maxMoveIndex + highMoveOffset - battleAnimationScriptsOffset;

        Map<String, Integer> auxiliaryAnimationScriptIndices = new HashMap<>();
        auxiliaryAnimationScriptIndices.put("infestation_trap", maxMoveScriptIndex + 1);

        while (battleAnimationScriptsNarc.files.size() < maxMoveScriptIndex)
            battleAnimationScriptsNarc.files.add(getDefaultAnimationScript());

        // 12
//        arm9.writeByte(0x0215339C, 0x24);

        // 36
//        arm9.writeByte(0x021AC538, 0x24);
//        arm9.writeByte(0x021ADB32, 0x24);
//        arm9.writeByte(0x021ADB56, 0x24);
//        arm9.writeByte(0x021ADB7A, 0x24);

        // Name Updates
        moves.get(Moves.viseGrip).name = "Vise Grip"; // 011
        moves.get(Moves.feintAttack).name = "Feint Attack"; // 185
        moves.get(Moves.smellingSalts).name = "Smelling Salts"; // 265

        switch (mode) {
            case ParagonLite -> loadMovesFromFile();
            case Redux -> loadReduxMoves();
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

        // All sound moves hit through substitute
        for (Move m : moves) {
            if (m != null && m.isSoundMove) m.bypassesSubstitute = true;
        }

        // Kick Moves
        for (int moveIndex : new int[]{
                Moves.stomp, // 023
                Moves.doubleKick, // 024
                Moves.jumpKick, // 026
                Moves.rollingKick, // 027
                Moves.lowKick, // 067
                Moves.highJumpKick, // 136
                Moves.tripleKick, // 167
                Moves.blazeKick, // 299
                Moves.lowSweep, // 490
        }) {
            moves.get(moveIndex).isCustomKickMove = true;
        }

        // Bite Moves
        for (int moveIndex : new int[]{
                Moves.bite, // 044
                Moves.hyperFang, // 158
                Moves.crunch, // 242
                Moves.poisonFang, // 305
                Moves.thunderFang, // 422
                Moves.iceFang, // 423
                Moves.fireFang, // 424
        }) {
            moves.get(moveIndex).isCustomBiteMove = true;
        }

        // Slice Moves
        for (int moveIndex : new int[]{
                Moves.cut, // 015
                Moves.razorLeaf, // 075
                Moves.slash, // 163
                Moves.furyCutter, // 210
                Moves.airCutter, // 314
                Moves.aerialAce, // 332
                Moves.leafBlade, // 348
                Moves.nightSlash, // 400
                Moves.airSlash, // 403
                Moves.xScissor, // 404
                Moves.psychoCut, // 427
                Moves.crossPoison, // 440
                Moves.sacredSword, // 533
                Moves.razorShell, // 534
                Moves.secretSword, // 548
        }) {
            moves.get(moveIndex).isCustomSliceMove = true;
        }

        // Triage
        for (Move move : moves) {
            if (move == null) continue;

            if (move.isHealMove || move.recoil > 0)
                move.isCustomTriageMove = true;
        }

        // Powder moves
        for (int moveIndex : new int[]{
                Moves.poisonPowder, // 077
                Moves.stunSpore, // 078
                Moves.sleepPowder, // 079
                Moves.spore, // 147
                Moves.cottonSpore, // 178
                Moves.ragePowder, // 476
        }) {
            moves.get(moveIndex).isCustomPowderMove = true;
        }

        // Wind moves
        for (int moveIndex : new int[]{
                Moves.razorWind, // 013
                Moves.gust, // 016
                Moves.whirlwind, // 018
                Moves.blizzard, // 059
                Moves.aeroblast, // 177
                Moves.icyWind, // 196
                Moves.twister, // 239
                Moves.heatWave, // 257
                Moves.airCutter, // 314
                Moves.silverWind, // 318
                Moves.tailwind, // 366
                Moves.ominousWind, // 466
                Moves.hurricane, // 542
        }) {
            moves.get(moveIndex).isCustomWindMove = true;
        }

        // Ball/Bomb moves
        for (int moveIndex : new int[]{
                Moves.eggBomb, // 121
                Moves.barrage, // 140
                Moves.sludgeBomb, // 188
                Moves.octazooka, // 190
                Moves.zapCannon, // 192
                Moves.shadowBall, // 247
                Moves.mistBall, // 296
                Moves.iceBall, // 301
                Moves.weatherBall, // 311
                Moves.bulletSeed, // 331
                Moves.rockBlast, // 350
                Moves.gyroBall, // 360
                Moves.auraSphere, // 396
                Moves.seedBomb, // 402
                Moves.focusBlast, // 411
                Moves.energyBall, // 412
                Moves.mudBomb, // 426
                Moves.rockWrecker, // 439
                Moves.magnetBomb, // 443
                Moves.electroBall, // 486
                Moves.acidSpray, // 491
                Moves.searingShot, // 545
        }) {
            moves.get(moveIndex).isCustomBallBombMove = true;
        }

        // Pulse Moves
        for (int moveIndex : new int[]{
                Moves.waterPulse, // 352
                Moves.auraSphere, // 396
                Moves.darkPulse, // 399
                Moves.dragonPulse, // 406
                Moves.healPulse, // 505
        }) {
            moves.get(moveIndex).isCustomPulseMove = true;
        }


        int[] newMoves;
        int[] movesToClear;
        switch (mode) {
            case ParagonLite -> {
                newMoves = new int[]{
                        Moves.doubleKick, // #024
                        Moves.twineedle, // #041
                        Moves.eggBomb, // #121
                        Moves.bonemerang, // #155
                        Moves.astonish, // #310
                        Moves.howl, // #336
                        Moves.magnetBomb, // #443
                        Moves.doubleHit, // #458
                        Moves.dualChop, // #530
                        Moves.gearGrind, // #544
                        Moves.stickyWeb, // #564
                        Moves.fellStinger, // #565
                        Moves.freezeDry, // #573
                        Moves.spikyShield, // #596
                        Moves.firstImpression, // #660
                        Moves.darkestLariat, // #663
                        Moves.sparklingAria, // #664
                        Moves.pollenPuff, // #676
                        Moves.psychicFangs, // #706
                        Moves.bodyPress, // #776
                        Moves.meteorBeam, // #800
                        Moves.flipTurn, // #812
                        Moves.tripleAxel, // #813
                        Moves.dualWingbeat, // #814
                        Moves.surgingStrikes, // #818
                        Moves.direClaw, // #827
                        Moves.ragingFury, // #833
                        Moves.barbBarrage, // #839
                        Moves.bitterMalice, // #841
                        Moves.infernalParade, // #844
                        Moves.makeItRain, // #874
                        Moves.electroShot, // #905
                        Moves.thunderclap, // #909
                        Moves.hardPress, // #912
                        Moves.supercellSlam, // #916
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
                        Moves.doubleKick, // #024
                        Moves.twineedle, // #041
                        Moves.eggBomb, // #121
                        Moves.bonemerang, // #155
                        Moves.octazooka, // #190
                        Moves.howl, // #336
                        Moves.magnetBomb, // #443
                        Moves.doubleHit, // #458
                        Moves.dualChop, // #530
                        Moves.gearGrind, // #544
                        Moves.stickyWeb, // #564
                        Moves.fellStinger, // #565
                        Moves.freezeDry, // #573
                        Moves.spikyShield, // #596
                        Moves.firstImpression, // #660
                        Moves.darkestLariat, // #663
                        Moves.sparklingAria, // #664
                        Moves.pollenPuff, // #676
                        Moves.psychicFangs, // #706
                        Moves.bodyPress, // #776
                        Moves.flipTurn, // #812
                        Moves.tripleAxel, // #813
                        Moves.dualWingbeat, // #814
                        Moves.direClaw, // #827
                        Moves.ragingFury, // #833
                        Moves.barbBarrage, // #839
                        Moves.bitterMalice, // #841
                        Moves.infernalParade, // #844
                        Moves.makeItRain, // #874
                        Moves.electroShot, // #905
                        Moves.thunderclap, // #909
                        Moves.hardPress, // #912
                        Moves.supercellSlam, // #916
                };

                movesToClear = new int[]{
                        Moves.chatter, // #448
                };
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

        int totalMoves = 918;
        int moveDataSize = 36;
        arm9.replaceData(new byte[moveDataSize * totalMoves], "Data_MoveCache");

//        arm9.writeHalfword(0x0204A8DA, 0xE005);

//        arm9.writeByte(0x020143C2, 0x24);
//        arm9.writeByte(0x020155C0, 0x24);
//        arm9.writeHalfword(0x020919D4, 0x0240);

        for (int moveToClear : movesToClear) {
            clearMoveEventHandlers(moveToClear);
        }

        // TODO: For some reason the relocator addresses values are wrong
        relocateMoveListRamAddress(newMoves.length - movesToClear.length);


        // HACK: Charge Beam has to be modified like this so we can get Electro Shot to work... idk
        setMoveAnimations(Moves.chargeBeam, 750);

        // TODO #564 Sticky Web
        moves.get(Moves.stickyWeb).name = "Sticky Web";
        setMoveEventHandlers(Moves.stickyWeb, new MoveEventHandler(Gen5BattleEventType.onUncategorizedMoveNoTarget, "sticky_web.s"));
        setMoveAnimations(Moves.stickyWeb, 770);

        // + #565 Fell Stinger
        moves.get(Moves.fellStinger).name = "Fell Stinger";
        setMoveEventHandlers(Moves.fellStinger, new MoveEventHandler(Gen5BattleEventType.onDamageProcessingEnd_HitReal, "fell_stinger.s"));
        setMoveAnimations(Moves.fellStinger);

        // #570 Parabolic Charge
        moves.get(Moves.parabolicCharge).name = "ParabolicCharge";
        setMoveAnimations(Moves.parabolicCharge);

        // + #573 Freeze-Dry
        moves.get(Moves.freezeDry).name = "Freeze-Dry";
        setMoveEventHandlers(Moves.freezeDry, new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "freeze-dry.s"));
        setMoveAnimations(Moves.freezeDry);

        // #574 Disarming Voice
        moves.get(Moves.disarmingVoice).name = "Disarming Voice";
        setMoveAnimations(Moves.disarmingVoice, 743);

        // #577 Draining Kiss
        moves.get(Moves.drainingKiss).name = "Draining Kiss";
        setMoveAnimations(Moves.drainingKiss, 744);

        // #583 Play Rough
        moves.get(Moves.playRough).name = "Play Rough";
        setMoveAnimations(Moves.playRough, 740);

        // #584 Fairy Wind
        moves.get(Moves.fairyWind).name = "Fairy Wind";
        setMoveAnimations(Moves.fairyWind);

        // #585 Moonblast
        moves.get(Moves.moonblast).name = "Moonblast";
        setMoveAnimations(Moves.moonblast, 741, 742);

        // #586 Boomburst
        moves.get(Moves.boomburst).name = "Boomburst";
        setMoveAnimations(Moves.boomburst, 752);

        // #591 Diamond Storm
        moves.get(Moves.diamondStorm).name = "Diamond Storm";
        setMoveAnimations(Moves.diamondStorm);

        // #594 Water Shuriken
        moves.get(Moves.waterShuriken).name = "Water Shuriken";
        setMoveAnimations(Moves.waterShuriken, 763);

        // #595 Mystical Fire
        moves.get(Moves.mysticalFire).name = "Mystical Fire";
        setMoveAnimations(Moves.mysticalFire);

        // TODO: + #596 Spiky Shield
        moves.get(Moves.spikyShield).name = "Spiky Shield";
        cloneMoveEventHandlers(Moves.spikyShield, Moves.protect);
        setMoveAnimations(Moves.spikyShield, 772);

        // #598 Eerie Impulse
        moves.get(Moves.eerieImpulse).name = "Eerie Impulse";
        setMoveAnimations(Moves.eerieImpulse);

        // #605 Dazzling Gleam
        moves.get(Moves.dazzlingGleam).name = "Dazzling Gleam";
        setMoveAnimations(Moves.dazzlingGleam, 745);

        // #609 Nuzzle
        moves.get(Moves.nuzzle).name = "Nuzzle";
        setMoveAnimations(Moves.nuzzle);

        // TODO: + #611 Infestation
        moves.get(Moves.infestation).name = "Infestation";
        setMoveAnimations(Moves.infestation, 747);
        setMoveAuxiliaryAnimation(Moves.infestation, "trap", auxiliaryAnimationScriptIndices);

        // #612 Power-Up Punch
        moves.get(Moves.powerUpPunch).name = "Power-Up Punch";
        setMoveAnimations(Moves.powerUpPunch);

        // + #660 First Impression
        moves.get(Moves.firstImpression).name = "FirstImpression";
        cloneMoveEventHandlers(Moves.firstImpression, Moves.fakeOut);
        setMoveAnimations(Moves.firstImpression, 760);

        // + #663 Darkest Lariat
        moves.get(Moves.darkestLariat).name = "Darkest Lariat";
        cloneMoveEventHandlers(Moves.darkestLariat, Moves.chipAway);
        setMoveAnimations(Moves.darkestLariat);

        // TODO: + #664 Sparkling Aria
        moves.get(Moves.sparklingAria).name = "Sparkling Aria";
        setMoveAnimations(Moves.sparklingAria, 773);

        // #665 Ice Hammer
        moves.get(Moves.iceHammer).name = "Ice Hammer";
        setMoveAnimations(Moves.iceHammer);

        // #667 High Horsepower
        moves.get(Moves.highHorsepower).name = "High Horsepower";
        setMoveAnimations(Moves.highHorsepower, 739);

        // TODO: #668 Strength Sap
        moves.get(Moves.strengthSap).name = "Strength Sap";
        setMoveAnimations(Moves.strengthSap);

        // + #676 Pollen Puff
        moves.get(Moves.pollenPuff).name = "Pollen Puff";
        setMoveEventHandlers(Moves.pollenPuff,
                new MoveEventHandler(Gen5BattleEventType.onCheckDamageToRecover, "pollen_puff_set_mode.s"),
                new MoveEventHandler(Gen5BattleEventType.onApplyDamageToRecover, "pollen_puff_heal.s"));
        setMoveAnimations(Moves.pollenPuff, 769);

        // #679 Lunge
        moves.get(Moves.lunge).name = "Lunge";
        setMoveAnimations(Moves.lunge, 753);

        // #680 Fire Lash
        moves.get(Moves.fireLash).name = "Fire Lash";
        setMoveAnimations(Moves.fireLash);

        // #684 Smart Strike
        moves.get(Moves.smartStrike).name = "Smart Strike";
        setMoveAnimations(Moves.smartStrike, 751);

        // #693 Brutal Swing
        moves.get(Moves.brutalSwing).name = "Brutal Swing";
        setMoveAnimations(Moves.brutalSwing, 774);

        // TODO: +#694 Aurora Veil
        moves.get(Moves.auroraVeil).name = "Aurora Veil";
        setMoveAnimations(Moves.auroraVeil, 767);

//        // #705 Fleur Cannon
        moves.get(Moves.fleurCannon).name = "Fleur Cannon";
        setMoveAnimations(Moves.fleurCannon, 761);

        // #706 Psychic Fangs
        moves.get(Moves.psychicFangs).name = "Psychic Fangs";
        cloneMoveEventHandlers(Moves.psychicFangs, Moves.brickBreak);
        setMoveAnimations(Moves.psychicFangs, 748);

        // #709 Accelerock
        moves.get(Moves.accelerock).name = "Accelerock";
        setMoveAnimations(Moves.accelerock);

        // #710 Liquidation
        moves.get(Moves.liquidation).name = "Liquidation";
        setMoveAnimations(Moves.liquidation);

        // + #776 Body Press
        moves.get(Moves.bodyPress).name = "Body Press";
        setMoveEventHandlers(Moves.bodyPress, new MoveEventHandler(Gen5BattleEventType.onGetAttackingStat, "body_press.s"));
        setMoveAnimations(Moves.bodyPress, 771);

        // #784 Breaking Swipe
        moves.get(Moves.breakingSwipe).name = "Breaking Swipe";
        setMoveAnimations(Moves.breakingSwipe, 755);

        // #786 Overdrive
        moves.get(Moves.overdrive).name = "Overdrive";
        setMoveAnimations(Moves.overdrive);

        // #789 Spirit Break
        moves.get(Moves.spiritBreak).name = "Spirit Break";
        setMoveAnimations(Moves.spiritBreak);

        // #794 Meteor Assault
        moves.get(Moves.meteorAssault).name = "Meteor Assault";
        setMoveAnimations(Moves.meteorAssault);

        // #796 Steel Beam
        moves.get(Moves.steelBeam).name = "Steel Beam";
        setMoveAnimations(Moves.steelBeam, 762);

        // #799 Scale Shot
        moves.get(Moves.scaleShot).name = "Scale Shot";
        setMoveAnimations(Moves.scaleShot, 758);

        // + #800 Meteor Beam
        moves.get(Moves.meteorBeam).name = "Meteor Beam";
        if (mode == Mode.Redux) {
            setMoveAnimations(Moves.meteorBeam, "redux");
        } else {
//            setMoveEventHandlers(Moves.meteorBeam, new MoveEventHandler(Gen5BattleEventType.onChargeUpStartDone, "meteor_beam.s"));
//            setMoveAnimations(Moves.meteorBeam);
        }

        // + #812 Flip Turn
        moves.get(Moves.flipTurn).name = "Flip Turn";
        cloneMoveEventHandlers(Moves.flipTurn, Moves.uTurn);
        setMoveAnimations(Moves.flipTurn, 759);

        // + #813 Triple Axel
        moves.get(Moves.tripleAxel).name = "Triple Axel";
        switch (mode) {
            // No longer changes damage
            case ParagonLite -> setMoveEventHandlers(Moves.tripleAxel, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
            case Redux -> setMoveEventHandlers(Moves.tripleAxel,
                    new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "triple_axel_redux.s"),
                    new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
        setMoveAnimations(Moves.tripleAxel, 754);

        // + #814 Dual Wingbeat
        moves.get(Moves.dualWingbeat).name = "Dual Wingbeat";
        setMoveEventHandlers(Moves.dualWingbeat, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
        setMoveAnimations(Moves.dualWingbeat, 746);

        // #815 Scorching Sands
        moves.get(Moves.scorchingSands).name = "Scorching Sands";
        setMoveAnimations(Moves.scorchingSands);

        // #817 Wicked Blow
        moves.get(Moves.wickedBlow).name = "Wicked Blow";
        setMoveAnimations(Moves.wickedBlow);

        // + #818 Surging Strikes
        moves.get(Moves.surgingStrikes).name = "Surging Strikes";
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.surgingStrikes, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
        setMoveAnimations(Moves.surgingStrikes, 766);

        // + #827 Dire Claw
        moves.get(Moves.direClaw).name = "Dire Claw";
        setMoveEventHandlers(Moves.direClaw, new MoveEventHandler(Gen5BattleEventType.onAddCondition, "dire_claw.s"));
        setMoveAnimations(Moves.direClaw);

        // #828 Psyshield Bash
        moves.get(Moves.psyshieldBash).name = "Psyshield Bash";
        setMoveAnimations(Moves.psyshieldBash);

        // TODO: + #830 Stone Axe
        moves.get(Moves.stoneAxe).name = "Stone Axe";
        setMoveAnimations(Moves.stoneAxe);

        // #833 Raging Fury
        moves.get(Moves.ragingFury).name = "Raging Fury";
        cloneMoveEventHandlers(Moves.ragingFury, Moves.thrash);
        setMoveAnimations(Moves.ragingFury);

        // #834 Wave Crash
        moves.get(Moves.waveCrash).name = "Wave Crash";
        setMoveAnimations(Moves.waveCrash);

        // #838 Headlong Rush
        moves.get(Moves.headlongRush).name = "Headlong Rush";
        setMoveAnimations(Moves.headlongRush);

        // + #839 Barb Barrage
        moves.get(Moves.barbBarrage).name = "Barb Barrage";
        setMoveEventHandlers(Moves.barbBarrage, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "barb_barrage.s"));
        setMoveAnimations(Moves.barbBarrage, 757);

        // #840 Esper Wing
        moves.get(Moves.esperWing).name = "Esper Wing";
        setMoveAnimations(Moves.esperWing, 749);

        // + #841 Bitter Malice
        moves.get(Moves.bitterMalice).name = "Bitter Malice";
        cloneMoveEventHandlers(Moves.bitterMalice, Moves.hex);
        setMoveAnimations(Moves.bitterMalice);

        // + #844 Infernal Parade
        moves.get(Moves.infernalParade).name = "Infernal Parade";
        cloneMoveEventHandlers(Moves.infernalParade, Moves.hex);
        setMoveAnimations(Moves.infernalParade);

        // TODO: + #845 Ceaseless Edge
        moves.get(Moves.ceaselessEdge).name = "Ceaseless Edge";
        setMoveAnimations(Moves.ceaselessEdge);

        // #855 Lumina Crash
        moves.get(Moves.luminaCrash).name = "Lumina Crash";
        setMoveAnimations(Moves.luminaCrash);

        // #857 Jet Punch
        moves.get(Moves.jetPunch).name = "Jet Punch";
        setMoveAnimations(Moves.jetPunch);

        // #859 Spin Out
        moves.get(Moves.spinOut).name = "Spin Out";
        setMoveAnimations(Moves.spinOut);

        // TODO: + #861 Ice Spinner
        moves.get(Moves.iceSpinner).name = "Ice Spinner";
        setMoveAnimations(Moves.iceSpinner);

        // #866 Mortal Spin
        moves.get(Moves.mortalSpin).name = "Mortal Spin";
        setMoveAnimations(Moves.mortalSpin);

        // #870 Flower Trick
        moves.get(Moves.flowerTrick).name = "Flower Trick";
        setMoveAnimations(Moves.flowerTrick, 768);

        // + #874 Make It Rain
        moves.get(Moves.makeItRain).name = "Make It Rain";
        cloneMoveEventHandlers(Moves.makeItRain, Moves.payDay);
        setMoveAnimations(Moves.makeItRain, 776);

        // #885 Trailblaze
        moves.get(Moves.trailblaze).name = "Trailblaze";
        setMoveAnimations(Moves.trailblaze, 765);

        // TODO: + #889 Rage Fist
        moves.get(Moves.rageFist).name = "Rage Fist";
        setMoveAnimations(Moves.rageFist);

        // #890 Armor Cannon
        moves.get(Moves.armorCannon).name = "Armor Cannon";
        setMoveAnimations(Moves.armorCannon);

        // #891 Bitter Blade
        moves.get(Moves.bitterBlade).name = "Bitter Blade";
        setMoveAnimations(Moves.bitterBlade);

        // #895 Aqua Cutter
        moves.get(Moves.aquaCutter).name = "Aqua Cutter";
        setMoveAnimations(Moves.aquaCutter, 756);

        // + #905 Electro Shot
        moves.get(Moves.electroShot).name = "Electro Shot";
        setMoveEventHandlers(Moves.electroShot,
                new MoveEventHandler(Gen5BattleEventType.onCheckChargeUpSkip, "electro_shot_charge_up_skip.s"),
                new MoveEventHandler(Gen5BattleEventType.onChargeUpStart, "electro_shot_charge_up_start.s"));
        setMoveAnimations(Moves.electroShot, 630);

        // #909 Thunderclap
        moves.get(Moves.thunderclap).name = "Thunderclap";
        cloneMoveEventHandlers(Moves.thunderclap, Moves.suckerPunch);
        setMoveAnimations(Moves.thunderclap, 775);

        // + #912 Hard Press
        moves.get(Moves.hardPress).name = "Hard Press";
        cloneMoveEventHandlers(Moves.hardPress, Moves.wringOut);
        setMoveAnimations(Moves.hardPress, 764);

        // #916 Supercell Slam
        moves.get(Moves.supercellSlam).name = "Supercell Slam";
        cloneMoveEventHandlers(Moves.supercellSlam, Moves.jumpKick);
        setMoveAnimations(Moves.supercellSlam);

        // #917 Psychic Noise
        moves.get(Moves.psychicNoise).name = "Psychic Noise";
        setMoveAnimations(Moves.psychicNoise);


        ///////////////
        // OLD MOVES //
        ///////////////

        // + #024 Double Kick
        setMoveEventHandlers(Moves.doubleKick, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        // + #041 Twineedle
        setMoveEventHandlers(Moves.twineedle, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        // #059 Blizzard
        setMoveEventHandlers(Moves.blizzard, new MoveEventHandler(Gen5BattleEventType.onBypassAccuracyCheck, "blizzard.s"));

        // #074 Growth
        setMoveEventHandlers(Moves.growth, new MoveEventHandler(Gen5BattleEventType.onGetStatStageChangeValue, "growth.s"));

        // #076 Solar Beam
        setMoveEventHandlers(Moves.solarBeam,
                new MoveEventHandler(Gen5BattleEventType.onCheckChargeUpSkip, "solar_beam_charge_up_skip.s"),
                new MoveEventHandler(Gen5BattleEventType.onChargeUpStart),
                new MoveEventHandler(Gen5BattleEventType.onGetMovePower, "solar_beam_move_power.s"));

        // #087 Thunder
        setMoveEventHandlers(Moves.thunder,
                new MoveEventHandler(Gen5BattleEventType.onCheckSemiInvulnerable),
                new MoveEventHandler(Gen5BattleEventType.onBypassAccuracyCheck, "thunder_bypass_accuracy_check.s"),
                new MoveEventHandler(Gen5BattleEventType.onGetMoveAccuracy, "thunder_accuracy.s"));

        // + #121 Egg Bomb
        cloneMoveEventHandlers(Moves.eggBomb, Moves.psystrike);

        // + #155 Bonemerang
        setMoveEventHandlers(Moves.bonemerang, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        // #167 Triple Kick
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.tripleKick, new MoveEventHandler(Gen5BattleEventType.onGetHitCount));

        // + #190 Octazooka
        if (mode == Mode.Redux)
            setMoveEventHandlers(Moves.octazooka, new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "super_effective_vs_steel.s"));

        // #200 Outrage
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.outrage, Moves.revenge);

        // #234 Morning Sun
        setMoveEventHandlers(Moves.morningSun, new MoveEventHandler(Gen5BattleEventType.onRecoverHealth, "morning_sun.s"));

        // #235 Synthesis
        cloneMoveEventHandlers(Moves.synthesis, Moves.morningSun);

        // #236 Moonlight
        cloneMoveEventHandlers(Moves.moonlight, Moves.morningSun);

        // #237 Hidden Power
        setMoveEventHandlers(Moves.hiddenPower, new MoveEventHandler(Gen5BattleEventType.onGetMoveParam));

        // #243 Mirror Coat
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.mirrorCoat, Moves.eruption);

        // #282 Knock Off
//        setMoveEventHandlers(Moves.knockOff,
//                new MoveEventHandler(Gen5BattleEventType.onDamageProcessingEnd_HitReal),
//                new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "knock_off.s"));

        // + #310 Astonish
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.astonish, Moves.fakeOut);

        // #311 Weather Ball
        setMoveEventHandlers(Moves.weatherBall,
                new MoveEventHandler(Gen5BattleEventType.onGetMoveParam, "weather_ball_type.s"),
                new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "weather_ball_base_power.s"));

        // #327 Sky Uppercut
        setMoveEventHandlers(Moves.skyUppercut,
                new MoveEventHandler(Gen5BattleEventType.onCheckSemiInvulnerable),
                new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "sky_uppercut.s"));

        // + #336 Howl
        setMoveEventHandlers(Moves.howl, new MoveEventHandler(Gen5BattleEventType.onUncategorizedMoveNoTarget, "howl.s"));

        // #360 Gyro Ball
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.gyroBall, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "gyro_ball.s"));

        // #362 Brine
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.brine, new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "super_effective_vs_steel.s"));

        // #368 Metal Burst
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.metalBurst, Moves.eruption);

        // #381 Lucky Chant
        if (mode == Mode.ParagonLite)
            cloneMoveEventHandlers(Moves.luckyChant, Moves.focusEnergy);

        // + #443 Magnet Bomb
        cloneMoveEventHandlers(Moves.magnetBomb, Moves.psystrike);

        // #449 Judgment (needs update for Pixie plate)
        setMoveEventHandlers(Moves.judgment, new MoveEventHandler(Gen5BattleEventType.onGetMoveParam, "judgment.s"));

        // + #458 Double Hit
        setMoveEventHandlers(Moves.doubleHit, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        // #486 Electro Ball
        if (mode == Mode.ParagonLite)
            setMoveEventHandlers(Moves.electroBall, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "electro_ball.s"));

        // + #530 Dual Chop
        setMoveEventHandlers(Moves.dualChop, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        // #542 Hurricane
        cloneMoveEventHandlers(Moves.hurricane, Moves.thunder);

        // + #544 Gear Grind
        setMoveEventHandlers(Moves.gearGrind, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        System.out.println("Set moves");
    }

    private void setMoveAnimations(int moveNumber, int... spaFiles) {
        setMoveAnimations(moveNumber, null, spaFiles);
    }

    private void setMoveAnimations(int moveNumber, String specification, int... spaFiles) {
        // TODO: Make this a global param somewhere
        int highMoveOffset = 116;
        int battleAnimationScriptsOffset = 561;

        String formattedMoveName = moves.get(moveNumber).name.toLowerCase().replace(' ', '_');
        for (int spaFileNumber : spaFiles) {
            byte[] spaFileData = readBytes(String.format("moveanims/spa/%03d_%s_%03d.spa", moveNumber, formattedMoveName, spaFileNumber));
            while (moveAnimationsNarc.files.size() <= spaFileNumber) {
                moveAnimationsNarc.files.add(new byte[16]);
            }
            moveAnimationsNarc.files.set(spaFileNumber, spaFileData);
        }

        String scriptFilename;
        if (specification == null)
            scriptFilename = String.format("moveanims/scripts/%03d_%s.bin", moveNumber, formattedMoveName);
        else
            scriptFilename = String.format("moveanims/scripts/%03d_%s_%s.bin", moveNumber, formattedMoveName, specification);

        byte[] script = readBytes(scriptFilename);
        if (moveNumber > Moves.fusionBolt)
            battleAnimationScriptsNarc.files.set(moveNumber + highMoveOffset - battleAnimationScriptsOffset, script);
        else
            moveAnimationScriptsNarc.files.set(moveNumber, script);
    }

    private void setMoveAuxiliaryAnimation(int moveNumber, String mode, Map<String, Integer> auxiliaryAnimationScriptIndices, int... spaFiles) {
        String formattedMoveName = moves.get(moveNumber).name.toLowerCase().replace(' ', '_');
        for (int spaFileNumber : spaFiles) {
            byte[] spaFileData = readBytes(String.format("moveanims/spa/%03d_%s_%s_%03d.spa", moveNumber, formattedMoveName, mode, spaFileNumber));
            while (moveAnimationsNarc.files.size() <= spaFileNumber) {
                moveAnimationsNarc.files.add(new byte[16]);
            }
            moveAnimationsNarc.files.set(spaFileNumber, spaFileData);
        }
        byte[] script = readBytes(String.format("moveanims/scripts/%03d_%s_%s.bin", moveNumber, formattedMoveName, mode));

        String auxiliaryAnimationName = String.format("%s_%s", formattedMoveName, mode);
        if (!auxiliaryAnimationScriptIndices.containsKey(auxiliaryAnimationName))
            throw new RuntimeException(String.format("Could not find auxiliary animation with id \"%s\"", auxiliaryAnimationName));
        int auxiliaryAnimationScriptIndex = auxiliaryAnimationScriptIndices.get(auxiliaryAnimationName);

        while (battleAnimationScriptsNarc.files.size() <= auxiliaryAnimationScriptIndex)
            battleAnimationScriptsNarc.files.add(getDefaultAnimationScript());
        battleAnimationScriptsNarc.files.set(auxiliaryAnimationScriptIndex, script);
    }

    private void setBattleAnimation(int globalScriptNumber, String filename, int... SpaFiles) {
        // TODO: Make this a global param somewhere
        int battleAnimationScriptsOffset = 561;

        for (int spaFileNumber : SpaFiles) {
            byte[] spaFileData = readBytes(String.format("moveanims/spa/%s_%03d.spa", filename, spaFileNumber));
            while (moveAnimationsNarc.files.size() <= spaFileNumber) {
                moveAnimationsNarc.files.add(new byte[16]);
            }
            moveAnimationsNarc.files.set(spaFileNumber, spaFileData);
        }
        byte[] script = readBytes(String.format("moveanims/scripts/%s.bin", filename));
        battleAnimationScriptsNarc.files.set(globalScriptNumber - battleAnimationScriptsOffset, script);
    }

    private byte[] getDefaultAnimationScript() {
        byte[] defaultScript = moveAnimationScriptsNarc.files.get(0);
        return Arrays.copyOf(defaultScript, defaultScript.length);
    }

    private void loadMovesFromFile() {
        loadOnlyNewReduxMoves();

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
                move = moves.get(num);

                String nameStr = q.substring(5, q.length() - 1).trim();

                // validate move
                String regex = "[- ]";
                String moveNameCheck = move.name.replaceAll(regex, "");
                String nameStrCheck = nameStr.replaceAll(regex, "");
                if (!moveNameCheck.equalsIgnoreCase(nameStrCheck) && num <= Moves.fusionBolt) {
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
                    if (!value.startsWith("\"") || !value.endsWith("\""))
                        throw new RuntimeException(String.format("Value must be in quotations for %s: \"%s\"", key, value));
                    move.name = value.substring(1, value.length() - 1);
                    break;
                }
                case "Description": {
                    if (!value.startsWith("\"") || !value.endsWith("\""))
                        throw new RuntimeException(String.format("Value must be in quotations for %s: \"%s\"", key, value));
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
    }

    private void loadReduxMoves() {
        try {
            byte[] bytes = readBytes("redux_moves.narc");
            NARCArchive reduxMovesNarc = new NARCArchive(bytes);
            romHandler.loadMoves(reduxMovesNarc, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This is a temporary func to load in redux move data, but only
    // the new moves!
    // TODO: Remove this
    private void loadOnlyNewReduxMoves() {
        try {
            byte[] bytes = readBytes("redux_moves.narc");
            NARCArchive reduxMovesNarc = new NARCArchive(bytes);
            romHandler.loadMoves(reduxMovesNarc, 560);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        setItemEventHandlers(index,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "icy_rock_hail"),
                new ItemEventHandler(Gen5BattleEventType.onCheckActivation, "icy_rock_hail"),
                new ItemEventHandler(Gen5BattleEventType.onCheckItemReaction, "icy_rock_immune"));
    }

    void setSmoothRock() {
        int number = Items.smoothRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock summons a sandstorm\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "smooth_rock_sandstorm"),
                new ItemEventHandler(Gen5BattleEventType.onCheckActivation, "smooth_rock_sandstorm"),
                new ItemEventHandler(Gen5BattleEventType.onCheckItemReaction, "smooth_rock_immune"));
    }

    void setHeatRock() {
        int number = Items.heatRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock turns the sunlight harsh\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "heat_rock"),
                new ItemEventHandler(Gen5BattleEventType.onCheckActivation, "heat_rock"));
    }

    void setDampRock() {
        int number = Items.dampRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock makes it rain\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "damp_rock"),
                new ItemEventHandler(Gen5BattleEventType.onCheckActivation, "damp_rock"));
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

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "weakness_policy_on_hit"), new ItemEventHandler(Gen5BattleEventType.onUseItem, "weakness_policy_boost"));
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

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, "roseli_berry_super_effective_check"), new ItemEventHandler(Gen5BattleEventType.onPostDamageReaction, Items.occaBerry));
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

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onDamageProcessingStart, "fairy_gem_work"), new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "fairy_gem_damage_boost"), new ItemEventHandler(Gen5BattleEventType.onDamageProcessingEnd, Items.fireGem));
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

        setItemEventHandlers(index, new ItemEventHandler(Gen5BattleEventType.onGetHitCount, "loaded_dice"));
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

//        battleOvl.writeHalfword(0x021A9BB4, 0x2801);

        disableRandomness();

        List<Trainer> trainers = romHandler.getTrainers();

        for (Trainer tr : trainers) {
            if (tr.pokemon.isEmpty())
                continue;

            tr.setPokemonHaveCustomMoves(true);
            tr.setPokemonHaveItems(true);

            TrainerPokemon poke1 = tr.pokemon.get(0);
            poke1.pokemon = romHandler.getPokemon().get(Species.registeel);
            poke1.level = 15;
            poke1.abilitySlot = 1;
            poke1.moves = new int[]{Moves.recover, 0, 0, 0};
            poke1.heldItem = Items.sitrusBerry;
            poke1.IVs = 0;

            if (tr.pokemon.size() < 2)
                tr.pokemon.add(tr.pokemon.get(0).copy());
            if (tr.pokemon.size() < 3)
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

        romHandler.setTrainers(trainers, false, true);

        // Set debug AI Flag
//        for (Trainer tr : trainers) {
//            tr.aiFlags = (1 << 14);
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
//        int dsAddress = 0x0207AC88;
//        int dsiAddress = 0x0207AC84;
//
//        int ds = arm9.readWord(dsAddress);
//        int dsi = arm9.readWord(dsiAddress);
//
//        arm9.writeWord(dsAddress, ds | 0x02000000, false);
//        arm9.writeWord(dsiAddress, dsi | 0x02000000, false);
    }

    // Disables battle random value (0.9x-1.0x damage value)
    // Helpful for testing things and getting consistent results
    public void disableRandomness() {
        // Always do 100% damage, no variance

        // TODO: Reimplement this somehow
//        int battleRandAddress = battleOvl.find("64 21 08 1A 00 04 00 0C 78 43 64 21");
//        battleOvl.writeHalfword(battleRandAddress + 2, 0x2064);

        // Remove crit chance at no change to ratio
        int critChanceAddress = battleOvl.find(Gen5Constants.critChanceLocator);
        if (critChanceAddress < 0) critChanceAddress = battleOvl.find("1808020101");

        battleOvl.writeByte(critChanceAddress, 0);

//        // Remove bonus/drawback from natures
//        int natureStatAdjustAddress = globalAddressMap.getRamAddress(arm9, "Data_StatNatureAdjust");
//        int natureStatAddress = globalAddressMap.getRamAddress(arm9, "Data_NatureStat");
//        for (int i = 0; i < 5 * 25; ++i) {
//            arm9.writeByte(natureStatAdjustAddress + i, 0);
//            arm9.writeByte(natureStatAddress + i, 0);
//        }

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
        if (index < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", abilityNames.get(abilityNumber)));

        setBattleObject(abilityNumber, index, abilityListRamAddress, events);
    }

    private void cloneAbilityEventHandlers(int abilityNumber, int otherAbilityNumber) {
        int abilityListAddress = getAbilityListAddress();
        int listIndex = getBattleObjectIndex(abilityListAddress, getAbilityListCount(), abilityNumber);
        if (listIndex < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", abilityNames.get(abilityNumber)));

        int abilityRedirectorAddress = getAbilityRedirectorAddress(otherAbilityNumber);
        battleOvl.writeWord(abilityListAddress + listIndex * 8, abilityNumber, false);
        battleOvl.writeWord(abilityListAddress + listIndex * 8 + 4, abilityRedirectorAddress + 1, true);
    }

    private void setMoveEventHandlers(int moveNumber, MoveEventHandler... events) {
        int moveListAddress = getMoveListAddress();
        int moveListCount = getMoveListCount();
        int index = getBattleObjectIndex(moveListAddress, moveListCount, moveNumber);
        if (index < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", moves.get(moveNumber).name));

        setBattleObject(moveNumber, index, moveListAddress, events);
    }

    private void cloneMoveEventHandlers(int moveNumber, int otherMoveNumber) {
        int moveListAddress = getMoveListAddress();
        int listIndex = getBattleObjectIndex(moveListAddress, getMoveListCount(), moveNumber);
        if (listIndex < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", moves.get(moveNumber).name));

        int moveRedirectorAddress = getMoveRedirectorAddress(otherMoveNumber);
        battleOvl.writeWord(moveListAddress + listIndex * 8, moveNumber, false);
        battleOvl.writeWord(moveListAddress + listIndex * 8 + 4, moveRedirectorAddress + 1, true);
    }

    private void setItemEventHandlers(int itemNumber, ItemEventHandler... events) {
        int itemListAddress = getItemListAddress();
        int itemListCount = getItemListCount();
        int index = getBattleObjectIndex(itemListAddress, itemListCount, itemNumber);
        if (index < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", itemNames.get(itemNumber)));

        setBattleObject(itemNumber, index, itemListAddress, events);
    }

    private void cloneItemEventHandlersFromAbility(int itemNumber, int abilityNumber) {
        int itemListAddress = getItemListAddress();
        int listIndex = getBattleObjectIndex(itemListAddress, getItemListCount(), itemNumber);
        if (listIndex < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", itemNames.get(itemNumber)));

        int abilityRedirectorAddress = getAbilityRedirectorAddress(abilityNumber);
        battleOvl.writeWord(itemListAddress + listIndex * 8, itemNumber, false);
        battleOvl.writeWord(itemListAddress + listIndex * 8 + 4, abilityRedirectorAddress + 1, true);
    }

    private void clearMoveEventHandlers(int moveNumber) {
        int moveListAddress = getMoveListAddress();
        int moveListCount = getMoveListCount();
        int index = getBattleObjectIndex(moveListAddress, moveListCount, moveNumber);
        if (index < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", moves.get(moveNumber).name));

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

    public static List<String> readLines(String filename) {
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

    private static byte[] readBytes(String filename) {
        byte[] bytes;
        try {
            InputStream stream = FileFunctions.openConfig("paragonlite/" + filename);

            if (stream == null)
                throw new IOException(String.format("Could not find file with name '%s'", filename));

            bytes = new byte[stream.available()];
            if (stream.read(bytes) == -1)
                throw new IOException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;
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

        byte[] bytes = readBytes(String.format("sprites/item/%s", sprite));

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

            BitmapFile.GraphicsFileParams params = new BitmapFile.GraphicsFileParams();
            params.width = 32;
            params.height = 32;

            byte[] graphicFile = bitmapFile.writeGraphicFile(params);
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

    private void setUISprite(int spriteNumber, String filename, BitmapFile.GraphicsFileParams params) {
        setUISprite(spriteNumber, -1, filename, params);
    }

    private void setUISprite(int spriteNumber, int paletteNumber, String filename, BitmapFile.GraphicsFileParams params) {
        String extension = ".bmp";
        if (!filename.endsWith(extension))
            filename += extension;

        byte[] bytes = readBytes(String.format("sprites/ui/%s", filename));

        BitmapFile bitmapFile;
        try {
            bitmapFile = new BitmapFile(bytes);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }

        byte[] graphicFile = bitmapFile.writeGraphicFile(params);
        battleUIGraphicsNarc.files.set(spriteNumber, graphicFile);

        if (paletteNumber < 0)
            return;

        byte[] paletteFile = bitmapFile.writePaletteFile();
        battleUIGraphicsNarc.files.set(paletteNumber, paletteFile);
    }

    private void writeTrainerAIFile(NARCArchive narc, int number) {
        byte[] bytes;
        try {
            InputStream stream = FileFunctions.openConfig(String.format("paragonlite/trainerai/scripts/%d.bin", number));
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
