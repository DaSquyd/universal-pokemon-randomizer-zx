package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.*;
import com.dabomstew.pkrandom.arm.ArmDecoder;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.arm.exceptions.ArmDecodeException;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.hack.*;
import com.dabomstew.pkrandom.romhandlers.hack.ability.old.*;
import com.dabomstew.pkrandom.romhandlers.hack.item.ItemProtectorMode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public class ParagonLiteHandler {

    public static class Params {
        Gen5RomHandler romHandler;
        Gen5RomHandler.RomEntry romEntry;

        HackMode hackMode;

        byte[] arm9Data;

        Pokemon[] classicPokes;
        Pokemon[] pokes;
        Map<Integer, FormeInfo> formeMappings;
        List<Move> moves;

        NARCArchive stringsNarc;
        NARCArchive pokemonGraphicsNarc;
        NARCArchive moveAnimationsNarc;
        NARCArchive itemDataNarc;
        NARCArchive itemGraphicsNarc;
        NARCArchive moveAnimationScriptsNarc;
        NARCArchive battleAnimationScriptsNarc;
        NARCArchive battleUIGraphicsNarc;
        NARCArchive moveBackgroundsNarc;
        NARCArchive trainerAIScriptsNarc;
        NARCArchive moveAnimatedBackgroundsNarc;

        List<String> unovaLinkStrings;

        List<String> battleStrings1;
        List<String> battleStrings2;
        List<String> battleStringsPokestar;

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

    Gen5RomHandler romHandler;
    Gen5RomHandler.RomEntry romEntry;
    Settings settings;

    ArmParser armParser;
    ParagonLiteAddressMap globalAddressMap;

    ParagonLiteArm9 arm9;
    SortedMap<OverlayId, ParagonLiteOverlay> overlays = new TreeMap<>();

    Pokemon[] classicPokes;
    Pokemon[] pokes;
    Map<Integer, FormeInfo> formeMappings;
    List<Move> moves;

    NARCArchive stringsNarc;
    NARCArchive pokemonGraphicsNarc;
    NARCArchive moveAnimationsNarc;
    int originalMoveAnimationsNarcCount;
    NARCArchive itemDataNarc;
    NARCArchive itemGraphicsNarc;
    NARCArchive moveAnimationScriptsNarc;
    NARCArchive battleAnimationScriptsNarc;
    NARCArchive battleUIGraphicsNarc;
    NARCArchive moveBackgroundsNarc;
    NARCArchive trainerAIScriptsNarc;
    NARCArchive moveAnimatedBackgroundsNarc;

    List<String> unovaLinkStrings;

    List<String> battleStrings1;
    List<String> battleStrings2;
    List<String> battleStringsPokestar;

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

    final HackMode vanillaHackMode = new HackMode("Vanilla");
    HackMode hackMode;
    boolean debugMode;

    private static class PokeUpdate {
        int hp;
        int attack;
        int defense;
        int spatk;
        int spdef;
        int speed;

        int type1;
        int type2;

        int catchRate;

        int itemGuaranteed;
        int itemCommon;
        int itemRare;
        int itemDarkGrass;

        int genderRatio;

        int baseFriendship;
        int growthRate;

        int eggGroup1;
        int eggGroup2;

        int ability1;
        int ability2;
        int ability3;

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

    ParagonLiteOverlay MakeOverlay(OverlayId overlayId, ParagonLiteOverlay.Insertion insertionType) {
        int ovlNumber = romHandler.isLowerVersion() ? overlayId.lowerVersionNumber : overlayId.upperVersionNumber;

        if (ovlNumber == 0)
            throw new RuntimeException();

        byte[] data;
        try {
            data = romHandler.readOverlay(ovlNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int address = romHandler.getOverlayAddress(ovlNumber);
        ParagonLiteOverlay overlay = new ParagonLiteOverlay(romHandler, ovlNumber, overlayId.name, data, address, insertionType, armParser, globalAddressMap);
        overlays.put(overlayId, overlay);
        return overlay;
    }

    ParagonLiteHandler(Settings settings, Params params) {
        romHandler = params.romHandler;
        romEntry = params.romEntry;
        hackMode = params.hackMode;
        this.settings = settings;

        globalAddressMap = new ParagonLiteAddressMap();
        armParser = new ArmParser(globalAddressMap);
        armParser.addGlobalValue("PARAGONLITE", hackMode.name.equals("ParagonLite"));
        armParser.addGlobalValue("REDUX", hackMode.name.equals("Redux"));
        armParser.addGlobalValue("LOWER_VERSION", romHandler.isLowerVersion());
        armParser.addGlobalValue("UPPER_VERSION", romHandler.isUpperVersion());
        armParser.addGlobalValue("BLACK", romHandler.isBlack());
        armParser.addGlobalValue("WHITE", romHandler.isWhite());
        armParser.addGlobalValue("BLACK_2", romHandler.isBlack2());
        armParser.addGlobalValue("WHITE_2", romHandler.isWhite2());
        armParser.addGlobalValue("HAS_POKESTAR_STUDIOS", romHandler.isUpperVersion());

        arm9 = new ParagonLiteArm9(romHandler, params.arm9Data, armParser, globalAddressMap);

        ParagonLiteOverlay localOvl = MakeOverlay(OverlayId.LOCAL, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay fieldOvl = MakeOverlay(OverlayId.FIELD, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay scriptPWTOvl = MakeOverlay(OverlayId.SCRIPT_PWT, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay pwtBattleOvl = MakeOverlay(OverlayId.PWT_BATTLE, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay uiCommonOvl = MakeOverlay(OverlayId.UI_COMMON, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay bagOvl = MakeOverlay(OverlayId.BAG, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay titleOvl = MakeOverlay(OverlayId.TITLE, ParagonLiteOverlay.Insertion.Front);
        ParagonLiteOverlay battleOvl = MakeOverlay(OverlayId.BATTLE, ParagonLiteOverlay.Insertion.Front);
        ParagonLiteOverlay battleLevelOvl = MakeOverlay(OverlayId.BATTLE_LEVEL, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay battleServerOvl = MakeOverlay(OverlayId.BATTLE_SERVER, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay trainerAIOvl = MakeOverlay(OverlayId.TRAINER_AI, ParagonLiteOverlay.Insertion.Front);
        ParagonLiteOverlay storageSystemOvl = MakeOverlay(OverlayId.STORAGE_SYSTEM, ParagonLiteOverlay.Insertion.Back);
        ParagonLiteOverlay metaSaveOvl = MakeOverlay(OverlayId.META_SAVE, ParagonLiteOverlay.Insertion.Restricted);
        ParagonLiteOverlay unovaLinkOvl = MakeOverlay(OverlayId.UNOVA_LINK, ParagonLiteOverlay.Insertion.Back);

//            // Search all overlays
//            for (int i = 0; i < 343; i++) {
//                byte[] ovl = romHandler.readOverlay(i);
//                int offset = romHandler.find(ovl, "F8 B5 82 B0 06 1C 15 48 1D 1C 00 90 08 A8 0F 1C");
//                if (offset < 0)
//                    continue;
//                
//                System.out.printf("found: ovl_%03d @ 0x%08X%n", i, offset);
//            }

        // Battle Overlays
        {
            ParagonLiteOverlay[] battleOverlays = new ParagonLiteOverlay[]{arm9, battleOvl, battleLevelOvl, battleServerOvl, trainerAIOvl};
            battleOvl.addContextOverlays(battleOverlays);
            battleLevelOvl.addContextOverlays(battleOverlays);
            battleServerOvl.addContextOverlays(battleOverlays);
            trainerAIOvl.addContextOverlays(battleOverlays);
            arm9.addContextOverlays(battleOverlays);
        }

        // Storage Overlays
        {
            ParagonLiteOverlay[] storageOverlays = new ParagonLiteOverlay[]{arm9, storageSystemOvl};
            storageSystemOvl.addContextOverlays(storageOverlays);
            arm9.addContextOverlays(storageOverlays);
        }

        // Title Overlays
        {
            ParagonLiteOverlay[] unovaLinkOverlays = new ParagonLiteOverlay[]{arm9, uiCommonOvl, titleOvl, unovaLinkOvl};
            titleOvl.addContextOverlays(unovaLinkOverlays);
            unovaLinkOvl.addContextOverlays(unovaLinkOverlays);
            arm9.addContextOverlays(unovaLinkOverlays);
        }

        // PWT Overlays
        {
            ParagonLiteOverlay[] pwtOverlays = new ParagonLiteOverlay[]{arm9, scriptPWTOvl, pwtBattleOvl};
            scriptPWTOvl.addContextOverlays(pwtOverlays);
            pwtBattleOvl.addContextOverlays(pwtOverlays);
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
            if (offsetStr.equals("--")) continue;

            if (!offsetStr.startsWith("0x"))
                throw new RuntimeException();

            offsetStr = offsetStr.substring(2); // remove 0x
            int romAddress = Integer.parseUnsignedInt(offsetStr, 16);

            if (romAddress == 0)
                throw new RuntimeException();

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
        decode(arm9, "TrTool_LoadParty");

        classicPokes = params.classicPokes;
        pokes = params.pokes;
        formeMappings = params.formeMappings;
        moves = params.moves;

        stringsNarc = params.stringsNarc;
        pokemonGraphicsNarc = params.pokemonGraphicsNarc;
        moveAnimationsNarc = params.moveAnimationsNarc;
        originalMoveAnimationsNarcCount = moveAnimationsNarc.files.size();
        itemDataNarc = params.itemDataNarc;
        itemGraphicsNarc = params.itemGraphicsNarc;
        moveAnimationScriptsNarc = params.moveAnimationScriptsNarc;
        battleAnimationScriptsNarc = params.battleAnimationScriptsNarc;
        battleUIGraphicsNarc = params.battleUIGraphicsNarc;
        moveBackgroundsNarc = params.moveBackgroundsNarc;
        moveAnimatedBackgroundsNarc = params.moveAnimatedBackgroundsNarc;

        unovaLinkStrings = params.unovaLinkStrings;

        battleStrings1 = params.battleStrings1;
        battleStrings2 = params.battleStrings2;
        battleStringsPokestar = params.battleStringsPokestar;

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

    public void applyHackMods() {
        hackMode.applyAll(romHandler, settings, armParser, globalAddressMap, arm9, overlays);
    }

    public void save() {
        System.out.print("Writing overlays");
        long startTime = System.currentTimeMillis();

        arm9.save(romHandler);
        for (ParagonLiteOverlay Overlay : overlays.values())
            Overlay.save(romHandler);

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
        setbattleEventStrings1();
        setBattleEventStrings2();
    }

    private enum BattleTextVar {
        TrainerName('\u0100'),
        SpeciesName('\u0101'),
        PokeNickname('\u0102'),
        Type('\u0103'),
        PokedexType('\u0104'),
        Place('\u0105'),
        AbilityName('\u0106'),
        MoveName('\u0107'),
        Nature('\u0108'),
        ItemName('\u0109');

        final char value;

        BattleTextVar(char value) {
            this.value = value;
        }
    }

    private String makeBattleString(String format, BattleTextVar... vars) {
        for (int i = 0; i < vars.length; i++) {
            BattleTextVar var = vars[i];
            format = format.replaceAll(String.format("\\{0*%d\\}", i), String.format("\uF000%c\\\\x0001\\\\x%04d", var.value, i));
        }
        return format.replaceAll("\n", "\\\\xFFFE");
    }

    private void setBattleStringWildFoe(int index, String format, BattleTextVar... vars) {
        String[] strings = makeBattleStringWildFoe(format, vars);
        for (int i = 0; i < strings.length; i++) {
            battleStrings1.set(index + i, makeBattleString(strings[i], vars));
        }
    }

    private void addBattleStringWildFoe(String format, BattleTextVar... vars) {
        String[] strings = makeBattleStringWildFoe(format, vars);

        for (String string : strings)
            battleStrings1.add(makeBattleString(string, vars));
    }

    private void addBattleStringStandard(String format, BattleTextVar... vars) {
        String string = makeBattleString(format, vars);
        int maxIndex = Math.max(battleStrings2.size(), battleStringsPokestar.size());
        battleStrings2.addAll(Collections.nCopies(maxIndex - battleStrings2.size(), ""));
        battleStringsPokestar.addAll(Collections.nCopies(maxIndex - battleStringsPokestar.size(), ""));

        string = makeBattleString(string, vars);
        battleStrings2.add(string);
        battleStringsPokestar.add(string);
    }

    private String[] makeBattleStringWildFoe(String format, BattleTextVar... vars) {
        if (vars.length < 1 || vars[0] != BattleTextVar.PokeNickname)
            throw new RuntimeException();

        if (format == null)
            throw new RuntimeException();

        String nicknameVarStr = "__NICKNAME_VAR__";
        Pattern pattern = Pattern.compile("\\{0+}");
        Matcher matcher = pattern.matcher(format);
        format = matcher.replaceAll(nicknameVarStr);

        String[] strings = new String[]{format, format, format};
        String[] formats = new String[]{"{0}", "%s wild {0}", "%s foe's {0}"};

        for (int i = 0; i < strings.length; i++) {
            for (int startIndex = strings[i].indexOf(nicknameVarStr); startIndex > -1; startIndex = strings[i].indexOf(nicknameVarStr)) {
                String theStr = isStartOfSentence(strings[i], startIndex) ? "The" : "the";

                strings[i] = strings[i].replaceFirst(nicknameVarStr, String.format(formats[i], theStr));
            }
        }

        return strings;
    }

    private boolean isStartOfSentence(String string, int offset) {
        // This isn't particularly robust, but it works for our purposes here

        if (offset == 0)
            return true;

        if (offset == 1)
            return string.charAt(0) == ' ';

        return (string.charAt(offset - 1) == '\n' || string.charAt(offset - 1) == ' ') && string.charAt(offset - 2) == '.';
    }

    private void addGlobalBattleTextValueStandard(String namespace, String message) {
        armParser.addGlobalValue(String.format("BTLTXT_%s_%s", namespace, message), Math.max(battleStrings2.size(), battleStringsPokestar.size()));
    }

    private void addGlobalBattleTextValueStandardCommon(String namespace, String message) {
        armParser.addGlobalValue(String.format("BTLTXT_Common_%s_%s", namespace, message), Math.max(battleStrings2.size(), battleStringsPokestar.size()));
    }

    private void addGlobalBattleTextValueWildFoe(String namespace, String message) {
        armParser.addGlobalValue(String.format("BTLTXT_%s_%s", namespace, message), battleStrings1.size());
    }

    private void addGlobalBattleTextValueWildFoeCommon(String namespace, String message) {
        armParser.addGlobalValue(String.format("BTLTXT_Common_%s_%s", namespace, message), battleStrings1.size());
    }

    private void setbattleEventStrings1() {
        // Heal Prevention (Heal Block + Psychic Noise)
        setBattleStringWildFoe(884, "{0}'s healing prevention\nwore off!", BattleTextVar.PokeNickname);
        setBattleStringWildFoe(887, "{0} was prevented\nfrom healing!", BattleTextVar.PokeNickname);
        setBattleStringWildFoe(890, "{0} can't use\n{0} because healing is prevented!", BattleTextVar.PokeNickname, BattleTextVar.MoveName);

        // Frostbite
        if (hackMode.statusFreezeReplaceWithFrostbite) {
            setBattleStringWildFoe(288, "{0} got\nfrostbite!", BattleTextVar.PokeNickname);
            setBattleStringWildFoe(291, "{0} was hurt\nby its frostbite!", BattleTextVar.PokeNickname);
            setBattleStringWildFoe(294, "{0}'s frostbite was healed!", BattleTextVar.PokeNickname);
            setBattleStringWildFoe(297, "{0} already\nhas frostbite!", BattleTextVar.PokeNickname);
            setBattleStringWildFoe(300, "{0} cannot\nget frostbite!", BattleTextVar.PokeNickname);
        }

        // Common Speed Was Not Lowered
        addGlobalBattleTextValueWildFoe("Common", "SpeedNotLowered");
        addBattleStringWildFoe("{0}'s Speed\nwas not lowered!", BattleTextVar.PokeNickname);

        // Polluted Terrain damage
        if (hackMode.includePollutedTerrain) {
            addGlobalBattleTextValueWildFoe("PollutedTerrain", "Hurt");
            addBattleStringWildFoe("{0} was hurt\nby the polluted terrain!", BattleTextVar.PokeNickname);
        }

        // Haunted Terrain damage
        if (hackMode.includeHauntedTerrain) {
            addGlobalBattleTextValueWildFoe("HauntedTerrain", "Hurt");
            addBattleStringWildFoe("{0} was hurt\nby the haunted terrain!", BattleTextVar.PokeNickname);
        }

        // #023 Shadow Tag
        if (hackMode.abilityShadowTagMessage != null) {
            addGlobalBattleTextValueWildFoe("ShadowTag", "Activate");
            addBattleStringWildFoe(hackMode.abilityShadowTagMessage, BattleTextVar.PokeNickname);
        }

        // #025 Wonder Guard
        if (hackMode.abilityWonderGuardMessage != null) {
            addGlobalBattleTextValueWildFoe("WonderGuard", "Activate");
            addBattleStringWildFoe(hackMode.abilityWonderGuardMessage, BattleTextVar.PokeNickname);
        }

        // #035 Illuminate
        if (hackMode.abilityIlluminateMode == AbilityIlluminateMode.BOOST_ALL_ACCURACY) {
            addGlobalBattleTextValueWildFoe("Illuminate", "Activate");
            addBattleStringWildFoe(hackMode.abilityIlluminateMessage, BattleTextVar.PokeNickname);
        }

        // #037 Huge Power
        if (hackMode.abilityHugePowerMessage != null) {
            addGlobalBattleTextValueWildFoe("HugePower", "Activate");
            addBattleStringWildFoe(hackMode.abilityHugePowerMessage, BattleTextVar.PokeNickname);
        }

        // #041 Water Veil
        if (hackMode.abilityWaterVeilMessage != null) {
            addGlobalBattleTextValueWildFoe("WaterVeil", "Activate");
            addBattleStringWildFoe(hackMode.abilityWaterVeilMessage, BattleTextVar.PokeNickname);
        }

        // #042 Magnet Pull
        if (hackMode.abilityMagnetPullMessage != null) {
            addGlobalBattleTextValueWildFoe("MagnetPull", "Activate");
            addBattleStringWildFoe(hackMode.abilityMagnetPullMessage, BattleTextVar.PokeNickname);
        }

        // #057 Plus
        if (hackMode.abilityPlusMessage != null) {
            addGlobalBattleTextValueWildFoe("Plus", "Activate");
            addBattleStringWildFoe(hackMode.abilityPlusMessage, BattleTextVar.PokeNickname);
        }

        // #058 Minus
        if (hackMode.abilityMinusMessage != null) {
            addGlobalBattleTextValueWildFoe("Minus", "Activate");
            addBattleStringWildFoe(hackMode.abilityMinusMessage, BattleTextVar.PokeNickname);
        }

        // #071 Arena Trap
        if (hackMode.abilityArenaTrapMessage != null) {
            addGlobalBattleTextValueWildFoe("ArenaTrap", "Activate");
            addBattleStringWildFoe(hackMode.abilityArenaTrapMessage, BattleTextVar.PokeNickname);
        }

        // #074 Pure Power
        if (hackMode.abilityPurePowerMessage != null) {
            addGlobalBattleTextValueWildFoe("PurePower", "Activate");
            addBattleStringWildFoe(hackMode.abilityPurePowerMessage, BattleTextVar.PokeNickname);
        }

        // #105 Super Luck
        if (hackMode.abilitySuperLuckMessage != null) {
            addGlobalBattleTextValueWildFoe("SuperLuck", "Activate");
            addBattleStringWildFoe(hackMode.abilitySuperLuckMessage, BattleTextVar.PokeNickname);
        }

        // #277 Wind Power
        addGlobalBattleTextValueWildFoe("WindPower", "Activate");
        addBattleStringWildFoe("Being hit by {1} charged\n{0} with power!", BattleTextVar.PokeNickname, BattleTextVar.MoveName);

        // #293 Supreme Overlord
        addGlobalBattleTextValueWildFoe("SupremeOverlord", "Activate");
        addBattleStringWildFoe("{0} gained strength\nfrom the fallen!", BattleTextVar.PokeNickname);

        // #506 Stone Home
        addGlobalBattleTextValueWildFoe("StoneHome", "Activate");
        addBattleStringWildFoe("{0} protects its allies\nwith its shell!", BattleTextVar.PokeNickname);

        // #508 Undercurrent
        addGlobalBattleTextValueWildFoe("Undercurrent", "Activate");
        addBattleStringWildFoe("{0} generated a\nstrong current for its team!", BattleTextVar.PokeNickname);

        // #512 X-ray Vision
        addGlobalBattleTextValueWildFoe("XrayVision", "Activate");
        addBattleStringWildFoe("{0} scanned its\ntarget and found one {0}!", BattleTextVar.PokeNickname, BattleTextVar.ItemName);

        // #522 Volcanic Fury
        addGlobalBattleTextValueWildFoe("VolcanicFury", "Activate");
        addBattleStringWildFoe("It burned {0}!", BattleTextVar.PokeNickname);


        // MOVES

        // #564 Sticky Web
        addGlobalBattleTextValueWildFoe("StickyWeb", "Enter");
        addBattleStringWildFoe("{0} was caught\nin a sticky web!", BattleTextVar.PokeNickname);

        // #596 Spiky Shield
        addGlobalBattleTextValueWildFoe("SpikyShield", "Activate");
        addBattleStringWildFoe("{0} was hurt\nby Spiky Shield!", BattleTextVar.PokeNickname);

        // #800 Meteor Beam
        addGlobalBattleTextValueWildFoe("MeteorBeam", "Charge");
        addBattleStringWildFoe("{0} is overflowing\nwith space power!", BattleTextVar.PokeNickname);

        // 809 Poltergeist
        addGlobalBattleTextValueWildFoe("Poltergeist", "Hit");
        addBattleStringWildFoe("{0} is about to be\nattacked by its {1}!", BattleTextVar.PokeNickname, BattleTextVar.ItemName);

        // #905 Electro Shot
        addGlobalBattleTextValueWildFoe("ElectroShot", "Charge");
        addBattleStringWildFoe("{0} absorbed\nelectricity!", BattleTextVar.PokeNickname);


        // ITEMS

        // #114 Assault Vest
        addGlobalBattleTextValueWildFoeCommon("StatusMovePreventItem", "StatusMoveAttempted");
        makeBattleStringWildFoe("{0} can't use {1}\nbecause of the {2}!", BattleTextVar.PokeNickname, BattleTextVar.MoveName, BattleTextVar.ItemName);

        // Weather Rocks, Plates
        addGlobalBattleTextValueWildFoeCommon("GlowItem", "Activate");
        addBattleStringWildFoe("{0}'s {1}\nbegan to glow!", BattleTextVar.PokeNickname, BattleTextVar.ItemName);
    }

    private void setBattleEventStrings2() {
        // Status move prevent item
        addGlobalBattleTextValueStandardCommon("StatusMovePreventItem", "StatusMoveSelected");
        addBattleStringStandard("The effects of the {0}\nprevent status moves from being used!", BattleTextVar.ItemName);

        // Sticky Web
        addGlobalBattleTextValueStandard("StickyWeb", "Laid");
        addBattleStringStandard("A sticky web has been laid out\non the ground around your team!");
        addBattleStringStandard("A sticky web has been laid out\non the ground around the opposing team!");

        addGlobalBattleTextValueStandard("StickyWeb", "Disappeared");
        addBattleStringStandard("The sticky web disappeared\nfrom around your team!");
        addBattleStringStandard("The sticky web disappeared\nfrom around the opposing team!");

        addGlobalBattleTextValueStandard("AuroraVeil", "Activated");
        addBattleStringStandard("Aurora veil made your team stronger\nagainst physical and special moves!");
        addBattleStringStandard("Aurora veil made the opposing team stronger\nagainst physical and special moves!");

        addGlobalBattleTextValueStandard("AuroraVeil", "Disappeared");
        addBattleStringStandard("Your team's\nAurora Veil disappeared!");
        addBattleStringStandard("The opposing team's\nAurora Veil disappeared!");
    }

    public void setDebugFlag(boolean isDebug) {
        armParser.addGlobalValue("DEBUG", isDebug);
        debugMode = isDebug;
    }

    public void addUtilFuncs() {
        arm9.writeCode(readLines("arm9/contains_byte.s"), "ContainsByte", true);
        arm9.writeCode(readLines("arm9/contains_halfword.s"), "ContainsHalfword", true);
        arm9.writeCode(readLines("arm9/contains_word.s"), "ContainsWord", true);
    }

    public void tempFixFairyStruggle() {
        // Redux already has Fairy implementation but still needs this bug fixed

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        int getMoveParamRomAddress = battleOvl.getRomAddress("ServerEvent_GetMoveParam");
        battleOvl.writeByte(getMoveParamRomAddress + 0x9A, 0x12);
    }

    public void setUnovaLink() {
        armParser.addGlobalValue("TITLETXT_UnovaLink_KeySystemButton", 69);
        armParser.addGlobalValue("TITLETXT_UnovaLink_MemoryLinkButton", 70);
        armParser.addGlobalValue("TITLETXT_UnovaLink_BackButton", 71);
        armParser.addGlobalValue("TITLETXT_UnovaLink_SubMenuDescriptions", 72);
        armParser.addGlobalValue("TITLETXT_UnovaLink_MainMenuTitle", 84);
        armParser.addGlobalValue("TITLETXT_UnovaLink_3dsLinkButton", 162);

//        // Send/Receive Keys is deprecated as all Keys are unlocked from the start
//        unovaLinkOvl.freeCode("KeySystem_SendReceive_Main");
//
//        unovaLinkOvl.freeCode("KeySystem_SendReceive_ReceiveDemo");
//        unovaLinkOvl.freeCode("KeySystem_SendReceive_SendDemo");
//
//        unovaLinkOvl.freeCode("KeySystem_CreateSendReceiveUI");
//
//        unovaLinkOvl.freeCode("KeySystem_CreateReceiveDemoUI");
//        unovaLinkOvl.freeCode("KeySystem_CreateSendDemoUI");
//        unovaLinkOvl.freeCode("KeySystem_CreateSendReceiveYesNoUI");
//
//        unovaLinkOvl.freeCode("KeySystem_UI_CreateSendReceive");
//        unovaLinkOvl.freeCode("KeySystem_UI_IsEndCreateSendReceive");
//        unovaLinkOvl.freeCode("KeySystem_UI_DestroySendReceive");
//
//        unovaLinkOvl.freeCode("KeySystem_UI_CreateReceiveDemo");
//        unovaLinkOvl.freeCode("KeySystem_UI_IsEndCreateReceiveDemo");
//        unovaLinkOvl.freeCode("KeySystem_UI_DestroyReceiveDemo");
//        unovaLinkOvl.freeCode("KeySystem_UI_CreateSendDemo");
//        unovaLinkOvl.freeCode("KeySystem_UI_IsEndCreateSendDemo");
//        unovaLinkOvl.freeCode("KeySystem_UI_DestroySendDemo");
//        unovaLinkOvl.freeCode("KeySystem_UI_CreateSendReceiveYesno");
//        unovaLinkOvl.freeCode("KeySystem_UI_IsEndCreateSendReceiveYesno");
//        unovaLinkOvl.freeCode("KeySystem_UI_DestroySendReceiveYesno");
//
//        unovaLinkOvl.freeCode("KeySystem_NetCallBack_SendReceiveEnd");
//
//        unovaLinkOvl.freeData("Data_KeySystem_UI_SendReceive");
//        unovaLinkOvl.freeData("Data_KeySystem_UI_SendReceive_Receive");
//        unovaLinkOvl.freeData("Data_KeySystem_UI_SendReceive_Send");
//        unovaLinkOvl.freeData("Data_KeySystem_UI_SendReceive_YesNo");

        ParagonLiteOverlay unovaLinkOvl = overlays.get(OverlayId.UNOVA_LINK);

        // UI_CreateMainMenu
        {
            List<String> lines = readLines("arm9/unovalink/ui_create_main_menu.s");
            unovaLinkOvl.writeCodeForceInline(lines, "UI_CreateMainMenu", false);
        }

        // Menu_Init
        {
            List<String> lines = readLines("arm9/unovalink/menu_init.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_Init", true);
        }

        // Menu_Exit
        {
            List<String> lines = readLines("arm9/unovalink/menu_exit.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_Exit", false);
        }

        // Menu_Main
        {
            List<String> lines = readLines("arm9/unovalink/menu_main.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_Main", false);
        }

        // Menu_Draw
        {
            List<String> lines = readLines("arm9/unovalink/menu_draw.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_Draw", false);
        }

        // Menu_IsDrawComplete
        {
            List<String> lines = readLines("arm9/unovalink/menu_is_draw_complete.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_IsDrawComplete", true);
        }

        // Menu_IsSelected
        {
            List<String> lines = readLines("arm9/unovalink/menu_is_selected.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_IsSelected", true);
        }

        // Menu_Reset
        {
            List<String> lines = readLines("arm9/unovalink/menu_reset.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_Reset", true);
        }

        // Menu_GetParam
        {
            List<String> lines = readLines("arm9/unovalink/menu_get_param.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_GetParam", true);
        }

        // Menu_GetCursor
        {
            List<String> lines = readLines("arm9/unovalink/menu_get_cursor.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_GetCursor", true);
        }

        // Menu_IsChanging
        {
            List<String> lines = readLines("arm9/unovalink/menu_is_changing.s");
            unovaLinkOvl.writeCodeForceInline(lines, "Menu_IsChanging", true);
        }
    }

    public void setPokeData() {
        // TODO: Because of PokeStar Studios, these are different between versions!

        // Updates the personal data to allow for abilities up to index 1023
        List<String> readPersonalDatalines = readLines("read_poke_personal_data.s");
        arm9.writeCodeForceInline(readPersonalDatalines, "ReadPokePersonalData", false);

        // Updates the box data to allow for abilities up to index 1023
        // Also fixes the Azurill->Marill gender bug
        List<String> readBoxDataLines = readLines("read_poke_box_data.s");
        arm9.writeCodeForceInline(readBoxDataLines, "ReadPokeBoxData", true);

        // Updates the box data to allow for abilities up to index 1023
        // Also fixes the Azurill->Marill gender bug
        List<String> writeBoxDataLines = readLines("write_poke_box_data.s");
        arm9.writeCodeForceInline(writeBoxDataLines, "WritePokeBoxData", false);

        System.out.println("Set Poke Data");
    }

    public void setBoxPreview() {
        ParagonLiteOverlay storageSystemOvl = overlays.get(OverlayId.STORAGE_SYSTEM);

        // This is the function that creates the struct used for a Pokémon's preview in the PC.
        // We are essentially swapping the markings (which is given 2 bytes despite only using 1) and ability.
        List<String> makeBox2MainLines = readLines("storagesystem/make_box2_main.s");
        storageSystemOvl.writeCodeForceInline(makeBox2MainLines, "MakeBox2Main", false);

        // This function calls PreviewCore as well as gets the Box2Main's markings, which have been moved, so we adjust
        List<String> displayPreviewLines = readLines("storagesystem/display_preview.s");
        storageSystemOvl.writeCodeForceInline(displayPreviewLines, "DisplayPreview", false);

        // Update to use the correct ability string when filling out the display field for ability
        List<String> previewAbilityLines = readLines("storagesystem/preview_ability.s");
        storageSystemOvl.writeCodeForceInline(previewAbilityLines, "Preview_Ability", false);
    }

    public void fixChallengeModeLevelBug() {
        if (!romHandler.isUpperVersion())
            return;

        // Challenge Mode and Normal mode are broken in B2W2.
        // After updating the levels for the Pokémon in an enemy trainer's team, the game doesn't recalculate the stats
        // the Pokémon should have.
        List<String> lines = readLines("difficulty_adjust_poke_level.s");
        arm9.writeCodeForceInline(lines, "Difficulty_AdjustPokeLevel", true);
        System.out.println("Fixed Challenge/Easy Mode level bug");
    }

    public void setGetEffectiveWeather() {
        // Allows for Damp and Sun-Soaked to override the current weather for outgoing moves

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> getEffectiveWeatherLines = readLines("battle/get_effective_weather.s");
        battleOvl.writeCode(getEffectiveWeatherLines, "GetEffectiveWeather", true);

        List<String> getHandlerEffectiveWeatherLines = readLines("battle/handler_get_effective_weather.s");
        battleOvl.writeCode(getHandlerEffectiveWeatherLines, "Handler_GetEffectiveWeather", true);

        System.out.println("Set GetEffectiveWeather");
    }

    public void setWeatherPowerMod() {
        if (hackMode.weatherDamageModStrong == vanillaHackMode.weatherDamageModStrong)
            return;

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        armParser.addGlobalValue("WEATHER_MOD_STRONG", hackMode.weatherDamageModStrong);
        armParser.addGlobalValue("WEATHER_MOD_WEAK", hackMode.weatherDamageModWeak);
        List<String> stabLines = readLines("battle/serverevent/weather_power_mod.s");
        battleOvl.replaceCode(stabLines, "ServerEvent_WeatherPowerMod");

        System.out.println("Set Weather Power Mod");
    }

    public void setMonoTypeSTAB() {
        if (hackMode.singleTypeSTAB == vanillaHackMode.singleTypeSTAB && hackMode.multiTypeSTAB == vanillaHackMode.multiTypeSTAB)
            return;

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        armParser.addGlobalValue("STAB_SINGLE_TYPE", hackMode.singleTypeSTAB);
        armParser.addGlobalValue("STAB_MULTI_TYPE", hackMode.multiTypeSTAB);
        List<String> stabLines = readLines("battle/serverevent/stab.s");
        battleOvl.replaceCode(stabLines, "ServerEvent_STAB");

        System.out.println("Set Multi-Type STAB");
    }

    public void setCalcDamageOffensiveValue() {
        // Allows for stat to be modified (for Body Press)

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> lines = readLines("calc_damage_get_offensive_value.s");
        battleOvl.replaceCode(lines, "ServerEvent_GetOffensiveValue");
        System.out.println("Set damage calc defensive stat");
    }

    public void setCalcDamageDefensiveValue() {
        // Grants Ice-type Pokémon a 1.5x Defense boost in hail

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> lines = readLines("calc_damage_get_defensive_value.s");
        battleOvl.replaceCode(lines, "ServerEvent_GetDefensiveValue");
        System.out.println("Set damage calc defensive stat");
    }

    public void setCalcDamage() {
        // Modernizes critical hit damage
        // 2.0x -> 1.5x
        // Frostbite 0.5x damage for Special moves
        // Facade ignores

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        armParser.addGlobalValue("CRITICAL_HIT_MULTIPLIER", hackMode.criticalHitMultiplier);
        armParser.addGlobalValue("REPLACE_FREEZE_WITH_FROSTBITE", hackMode.statusFreezeReplaceWithFrostbite);
        armParser.addGlobalValue("ABILITY_COOLANT_BOOST", ParagonLiteAbilities.coolantBoost);
        List<String> lines = readLines("calc_damage.s");
        battleOvl.writeCodeForceInline(lines, "ServerEvent_CalcDamage", false);
        System.out.println("Set damage calc");
    }

    public void setCritRatio() {
        if (Arrays.equals(hackMode.criticalHitRatios, vanillaHackMode.criticalHitRatios))
            return;

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        armParser.addGlobalValue("CRITICAL_HIT_RATIO_MAX", hackMode.criticalHitRatios.length - 1);
        List<String> lines = readLines("check_critical_hit.s");
        battleOvl.writeCodeForceInline(lines, "CheckCriticalHit", false);
        battleOvl.writeData(hackMode.criticalHitRatios, "Data_CriticalHitChances");

        System.out.println("Set critical hit ratios");
    }

    public void setStatus() {
        // Modernizes Burn damage
        // 1/8 -> 1/16
        //
        // Sets Frostbite damage
        // 1/16

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        ParagonLiteOverlay battleLevelOvl = overlays.get(OverlayId.BATTLE_LEVEL);
        ParagonLiteOverlay battleServerOvl = overlays.get(OverlayId.BATTLE_SERVER);

        armParser.addGlobalValue("STATUS_BURN_DAMAGE_FRACTION", hackMode.statusBurnDamageFraction);
        armParser.addGlobalValue("STATUS_FREEZE_REPLACE_WITH_FROSTBITE", hackMode.statusFreezeReplaceWithFrostbite);
        armParser.addGlobalValue("STATUS_FROSTBITE_DAMAGE_FRACTION", hackMode.statusFrostbiteDamageFraction);
        List<String> statusDamageLines = readLines("get_status_damage.s");
        battleOvl.writeCodeForceInline(statusDamageLines, "GetStatusDamage", false);

        //////////////////////////////////////
        /// Replaces Freeze with Frostbite ///
        //////////////////////////////////////
        if (hackMode.statusFreezeReplaceWithFrostbite) {
            // Adds damage VFX
            List<String> conditionDamageRecallLines = readLines("condition_damage_recall.s");
            battleOvl.writeCodeForceInline(conditionDamageRecallLines, "ConditionDamageRecall", false);

            // sets frostbite damage string
            List<String> statusDamageStringLines = readLines("get_status_damage_string.s");
            battleServerOvl.writeCodeForceInline(statusDamageStringLines, "Condition_GetDamageText", true);

            // sets frostbite glow color
            List<String> effectMainUpdateConditionLines = readLines("effect_main_update_condition.s");
            battleLevelOvl.writeCodeForceInline(effectMainUpdateConditionLines, "EffectMain_UpdateCondition", true);

            // skips the move fail check for being frozen
            int moveExeCheck1FreezeRomAddress = globalAddressMap.getRomAddress(battleOvl, "ServerControl_MoveExeCheck1");
            battleOvl.writeHalfword(moveExeCheck1FreezeRomAddress + 0x3E, 0xE009);

            // skips the 20% chance of recovering from freeze
            int checkMoveExeFreezeThawRomAddress = globalAddressMap.getRomAddress(battleOvl, "ServerControl_CheckMoveExeFreezeThaw");
            battleOvl.writeHalfword(checkMoveExeFreezeThawRomAddress + 0x24, 0xD007);

            // being hit by a fire move will thaw the user out, but this isn't the case for frostbite
            int damageFreezeThawRomAddress = globalAddressMap.getRomAddress(battleOvl, "ServerControl_DamageFreezeThaw");
            battleOvl.writeHalfword(damageFreezeThawRomAddress, 0x4770); // Immediately exits the function with "bx lr"

            setBattleAnimation(601, "frostbite");

            BitmapFile.GraphicsFileParams conditionBadgesSpriteParams = new BitmapFile.GraphicsFileParams();
            conditionBadgesSpriteParams.subImageCount = 8;

            setUISprite(12, "condition_badges.bmp", conditionBadgesSpriteParams);
        }

        //////////////////////////////////
        /// Paralysis Speed 25% -> 50% ///
        //////////////////////////////////
        if (hackMode.statusParalysisSpeedPercent != vanillaHackMode.statusParalysisSpeedPercent) {
            int calculateSpeedRomAddress = globalAddressMap.getRomAddress(battleOvl, "ServerEvent_CalculateSpeed");
            battleOvl.writeByte(calculateSpeedRomAddress + 0x80, hackMode.statusParalysisSpeedPercent);
        }

        ///////////////////////////////////////
        /// Confusion hit chance 50% -> 33% ///
        ///////////////////////////////////////
        if (hackMode.statusConfusionHitPercent != vanillaHackMode.statusConfusionHitPercent) {
            int checkConfusionHitRomAddress = globalAddressMap.getRomAddress(battleOvl, "ServerControl_CheckConfusionHit");
            battleOvl.writeByte(checkConfusionHitRomAddress + 0x74, hackMode.statusConfusionHitPercent);
        }
    }

    public void setStatChangeIntimidateFlag() {
        // Updates the stat change functions to include a flag for a stat change being caused by intimidate

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> checkStatChangeSuccessLines = readLines("battle/serverevent/check_stat_change_success.s");
        battleOvl.writeCodeForceInline(checkStatChangeSuccessLines, "ServerEvent_CheckStatChangeSuccess", false);

        List<String> statStageChangeAppliedLines = readLines("battle/serverevent/stat_stage_change_applied.s");
        battleOvl.writeCodeForceInline(statStageChangeAppliedLines, "ServerEvent_StatStageChangeApplied", false);

        List<String> statStageChangeFailedLines = readLines("battle/serverevent/stat_stage_change_failed.s");
        battleOvl.writeCodeForceInline(statStageChangeFailedLines, "ServerEvent_StatStageChangeFailed", false);

        List<String> statStageChangeCoreLines = readLines("battle/servercontrol/stat_stage_change_core.s");
        battleOvl.writeCodeForceInline(statStageChangeCoreLines, "ServerControl_StatStageChangeCore", false);

        List<String> handlerChangeStatStageLines = readLines("battle/handler_change_stat_stage.s");
        battleOvl.writeCodeForceInline(handlerChangeStatStageLines, "Handler_ChangeStatStage", false);

        List<String> moveStatStageChangeEffectCommonLines = readLines("battle/servercontrol/move_stat_stage_change_effect_common.s");
        battleOvl.writeCodeForceInline(moveStatStageChangeEffectCommonLines, "ServerControl_MoveStatStageChangeEffectCommon", false);
    }

    public void setTrapDamage() {
        if (hackMode.trapMoveDamageFraction == vanillaHackMode.statusBurnDamageFraction
                && hackMode.trapMoveDamageFractionWithBoost == vanillaHackMode.trapMoveDamageFractionWithBoost)
            return;

        ParagonLiteOverlay battleServerOvl = overlays.get(OverlayId.BATTLE_SERVER);

        // Modernizes the damage of the Trapped condition
        // 1/16 -> 1/8 without Binding Band
        // 1/8  -> 1/6 with Binding Band
        int conditionHandlerBindFuncAddress = globalAddressMap.getRamAddress(battleServerOvl, "Condition_HandlerBind");

        battleServerOvl.writeByte(conditionHandlerBindFuncAddress + 0x94, hackMode.trapMoveDamageFraction);
        battleServerOvl.writeByte(conditionHandlerBindFuncAddress + 0x8E, hackMode.trapMoveDamageFractionWithBoost);

//        // TODO: finish this
//        List<String> conditionHandlerBindLines = readLines("battleserver/condition_handler_bind.s");
//        battleServerOvl.writeCodeForceInline(conditionHandlerBindLines, "Condition_HandlerBind", true);

        System.out.println("Set Trapped damage");
    }

    public void setTypeForPlate() {
        // Adds Pixie Plate functionality for Judgment and Arceus
        List<String> lines = readLines("get_type_for_plate.s");
        arm9.writeCodeForceInline(lines, "GetTypeForPlate", false);

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
        if (hackMode.gemItemDamageMultiplier == vanillaHackMode.gemItemDamageMultiplier)
            return;

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        // Modernizes Gem damage
        // 1.5x -> 1.3x
        armParser.addGlobalValue("GEM_ITEM_DAMAGE_MULTIPLIER", hackMode.gemItemDamageMultiplier);
        List<String> lines = readLines("gem_damage_boost.s");
        battleOvl.replaceCode(lines, "CommonGemDamageBoost");

        System.out.println("Set Gem damage boost");
    }

    public void setMultiStrikeLoadedDice() {
        // Implements logic for Loaded Dice
        // Multi-strike moves with multiple accuracy checks (like Triple Kick) only evaluate once
        // Moves that hit 2-5 times will always do 4 or 5 hits

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> lines = readLines("set_multi_strike_data.s");
        battleOvl.replaceCode(lines, "SetMultiStrikeData");

        System.out.println("Set multi-strike");
    }

    public void setIsUnselectableMove() {
        // Implements logic for Assault Vest and Protector

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        armParser.addGlobalValue("ITEM_PROTECTOR_NO_STATUS", hackMode.itemProtectorMode == ItemProtectorMode.DEFENSE_BOOST_NO_STATUS);
        List<String> lines = readLines("is_unselectable_move.s");
        battleOvl.replaceCode(lines, "IsUnselectableMove");

        System.out.println("Set IsUnselectableMove");
    }

    public void setWeatherDamage() {
        if (Arrays.equals(hackMode.weatherHailImmuneTypes, vanillaHackMode.weatherHailImmuneTypes)
                && Arrays.equals(hackMode.weatherSandstormImmuneTypes, vanillaHackMode.weatherSandstormImmuneTypes))
            return;

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        for (Type type : hackMode.weatherHailImmuneTypes)
            armParser.addGlobalValue(String.format("WEATHER_HAIL_%s_IMMUNE", type.toString()), true);

        for (Type type : hackMode.weatherSandstormImmuneTypes)
            armParser.addGlobalValue(String.format("WEATHER_SAND_%s_IMMUNE", type.toString()), true);

        // Grants Fighting-type Pokémon immunity to hail
        List<String> lines = readLines("poke_calc_weather_damage.s");
        battleOvl.replaceCode(lines, "Poke_CalcWeatherDamage");

        System.out.println("Set weather damage");
    }

    public void setShinyRate() {
        armParser.addGlobalValue("SHINY_RATE", hackMode.shinyRate);
        if (hackMode.shinyRate == vanillaHackMode.shinyRate)
            return;

        // Increases shiny odds
        List<String> lines = readLines("shiny.s");
        arm9.writeCodeForceInline(lines, "IsShiny", true);

        System.out.println("Set shiny rate");
    }

    public void setTrainerShiny() {
        if (!hackMode.allowShinyTrainerPokemon)
            return;

        int createPokeAddress = globalAddressMap.getRomAddress(arm9, "PML_CreatePoke");
        arm9.writeByte(createPokeAddress + 0x82, 0x21); // branches

        System.out.println("Set trainer shiny");
    }

    public void setCanPokeEscape() {
        switch (hackMode.abilityPlusMode) {
            case VANILLA -> {
                if (!hackMode.typeGhostCanAlwaysEscape)
                    return;
            }
            // Allow Pokémon with Run Away to always escape
            case ALLY_SPECIAL_ATTACK -> armParser.addGlobalValue("ABILITY_RUN_AWAY_CAN_ESCAPE_TRAPS", true);
            default -> throw new IllegalStateException("Unexpected value: " + hackMode.abilityPlusMode);
        }

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        // Allow Ghost to always escape
        armParser.addGlobalValue("TYPE_GHOST_CAN_ALWAYS_ESCAPE", hackMode.typeGhostCanAlwaysEscape);

        List<String> lines = readLines("is_poke_trapped.s");
        battleOvl.writeCodeForceInline(lines, "IsPokeTrapped", false);

        System.out.println("Set Can Poke Escape");
    }

    public void setCheckNoEffect() {
        if (!hackMode.typeGrassIsImmuneToPowderMoves && !hackMode.typeDarkIsImmuneToPrankster)
            return;

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        armParser.addGlobalValue("TYPE_GRASS_IS_IMMUNE_TO_POWDER_MOVES", hackMode.typeGrassIsImmuneToPowderMoves);
        armParser.addGlobalValue("TYPE_DARK_IS_IMMUNE_TO_PRANKSTER", hackMode.typeDarkIsImmuneToPrankster);

        // Updates Dark to be immune to Prankster-boosted moves and Grass to be immune to Powder moves
        // Adds Spiky Shield check
        List<String> lines = readLines("battle/servercontrol/check_no_effect.s");
        battleOvl.writeCodeForceInline(lines, "ServerControl_CheckNoEffect", false);

        System.out.println("Set Check No Effect");
    }

    public void setCallModifyEffectivenessHandler() {
        // Updates the call handler for modifying effectiveness.
        // This is originally used for Scrappy, Ring Target, Miracle Eye, Foresight, Odor Sleuth, Ingrain, and Grounded.
        // Now, it allows for modification of type effectiveness beyond removing immunities
        // This is a necessary modification to handle 1/8 and x8 damage multipliers from effectiveness

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> lines = readLines("call_modify_effectiveness_handler.s");
        battleOvl.writeCodeForceInline(lines, "CallModifyEffectivenessHandler", false);

        System.out.println("Set Call Modify Effectiveness Handler");
    }

    public void setHandlerSimulationDamage() {
        if (!hackMode.aiSimulateDamageFix)
            return;

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        // Necessary so the AI can properly evaluate multi-strike moves
        // For moves that can hit a maximum of 5 times, 5x with Skill Link, 4.5x with Loaded Dice, and 3.1x otherwise
        List<String> multiStrikeMultiplierLines = readLines("handler_get_simulation_multi-strike_multiplier.s");
        battleOvl.writeCode(multiStrikeMultiplierLines, "Handler_GetSimulationMultiStrikeMultiplier", true);

        // Updates simulation damage to include move bindings for variable power, type, effectiveness, etc.
        List<String> simulationDamageLines = readLines("handler_simulation_damage.s");
        battleOvl.writeCodeForceInline(simulationDamageLines, "Handler_SimulationDamage", false);

        System.out.println("Set Handler Simulation Damage");
    }

    public void setScreenPower() {
        if (hackMode.screenMoveDoubleBattleReduction == vanillaHackMode.screenMoveDoubleBattleReduction)
            return;

        ParagonLiteOverlay battleServerOvl = overlays.get(OverlayId.BATTLE_SERVER);

        // Updates Light Screen and Reflect to use the proper 33% reduction seen in Gen VI onwards for double/triple battles 
        int romAddress = globalAddressMap.getRomAddress(battleServerOvl, "CommonScreenEffect");
        int screenMoveDoubleBattleReduction = (int) Math.round(0x1000 * hackMode.screenMoveDoubleBattleReduction);
        battleServerOvl.writeWord(romAddress + 0x4C, screenMoveDoubleBattleReduction, false);

        System.out.println("Set double/triple battle screen power");
    }

    public void setNewSideStatus() {
        int newCount = 2; // Sticky Web, Aurora Veil
        int oldTotal = 14;
        int newTotal = oldTotal + newCount;
        armParser.addGlobalValue("SIDE_STATUS_COUNT", newTotal);

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        ParagonLiteOverlay battleServerOvl = overlays.get(OverlayId.BATTLE_SERVER);

        int sideStatusEffectTable = globalAddressMap.getRomAddress(battleServerOvl, "Data_SideStatusEffectTable");

        byte[] newData = new byte[8 * newTotal];

        for (int i = 0; i < 14; ++i) {
            int sideStatusId = battleServerOvl.readWord(sideStatusEffectTable + i * 12);
            int sideStatusEffectRef = battleServerOvl.readWord(sideStatusEffectTable + i * 12 + 4);
            int sideStatusMaxLevel = battleServerOvl.readWord(sideStatusEffectTable + i * 12 + 8);

            writeHalf(newData, i * 8, sideStatusId);
            writeHalf(newData, i * 8 + 2, sideStatusMaxLevel);
            writeWord(newData, i * 8 + 4, sideStatusEffectRef);
        }

        addSideStatus(newData, oldTotal, 1, "StickyWeb", "sticky_web", Gen5BattleEventType.onSwitchIn);
        addSideStatus(newData, oldTotal + 1, 1, "AuroraVeil", "aurora_veil", Gen5BattleEventType.onMoveDamageProcessing2);

        // Write data to BattleOvl for space reasons
        battleServerOvl.writeBytes(sideStatusEffectTable, newData);

        List<String> initLines = readLines("battleserver/side_status_init.s");
        battleServerOvl.writeCodeForceInline(initLines, "SideStatus_Init", true);

        List<String> addItemLines = readLines("battleserver/side_status_add_item.s");
        battleServerOvl.writeCodeForceInline(addItemLines, "SideStatus_AddItem", true);

        List<String> getLevelLines = readLines("battleserver/side_status_get_level.s");
        battleServerOvl.writeCodeForceInline(getLevelLines, "SideStatus_GetLevel", true);

        List<String> isActiveLines = readLines("battleserver/side_status_is_active.s");
        battleServerOvl.writeCodeForceInline(isActiveLines, "SideStatus_IsActive", true);

        List<String> removeItemLines = readLines("battleserver/side_status_remove_item.s");
        battleServerOvl.writeCodeForceInline(removeItemLines, "SideStatus_RemoveItem", true);

        List<String> turnCheckLines = readLines("battleserver/side_status_turn_check.s");
        battleServerOvl.writeCodeForceInline(turnCheckLines, "SideStatus_TurnCheck", true);

        List<String> getLevelsFromEventItemLines = readLines("battleserver/side_status_get_level_from_event_item.s");
        battleServerOvl.writeCodeForceInline(getLevelsFromEventItemLines, "SideStatus_GetLevelFromEventItem", true);

        List<String> sideStatusEndMessageLines = readLines("battle/servercontrol/side_status_end_message_core.s");
        battleOvl.writeCodeForceInline(sideStatusEndMessageLines, "ServerControl_SideStatusEndMessageCore", true);

        // Replace hardcoded null values (only used by the pledge moves)
        int pledgeFuncRefAddress = getEventHandlerFuncReferenceAddress(Moves.waterPledge, getMoveListAddress(), getMoveListCount(), Gen5BattleEventType.onDamageProcessingEnd_Hit2);
        int pledgeFuncAddress = battleOvl.readWord(pledgeFuncRefAddress) - 1;
        int nullId = (Integer) armParser.getGlobalValue("SC_Null");
        battleOvl.writeByte(pledgeFuncAddress + 0x1C, nullId);
        battleOvl.writeByte(pledgeFuncAddress + 0x80, nullId);
    }

    private void addSideStatus(byte[] data, int statusId, int maxLevel, String name, String handlerName, int eventType) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> handlerLines = readLines(String.format("battleserver/handler_side_%s.s", handlerName));
        int handlerLinesRomAddress = battleOvl.writeCode(handlerLines, String.format("HandlerSide_%s", name), true);
        int handlerLinesRamAddress = battleOvl.romToRamAddress(handlerLinesRomAddress);

        byte[] eventTable = new byte[8];
        writeWord(eventTable, 0, eventType);
        writeWord(eventTable, 4, handlerLinesRamAddress + 1);
        battleOvl.writeData(eventTable, String.format("HandlerSide_%s_EventTable", name));

        List<String> eventAddLines = readLines(String.format("battleserver/side_status_event_add_%s.s", handlerName));
        int eventAddRomAddress = battleOvl.writeCode(eventAddLines, String.format("SideStatusEventAdd_%s", name), true);
        int eventAddRamAddress = battleOvl.romToRamAddress(eventAddRomAddress);

        writeHalf(data, statusId * 8, statusId);
        writeHalf(data, statusId * 8 + 2, maxLevel);
        writeWord(data, statusId * 8 + 4, eventAddRamAddress + 1);
    }

    public void setBattlePokeCreate() {
        // Expands BattlePoke to allow turnFlags to be two larger

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int pokeCreateRomAddress = globalAddressMap.getRomAddress(battleOvl, "Poke_Create");
        battleOvl.writeByte(pokeCreateRomAddress + 0x0A, 0x01FC >> 2);

        setBattlePokeCreate_Replace("Poke_GetTurnFlag", 0x28);
        setBattlePokeCreate_Replace("Poke_SetTurnFlag", 0x20);
        setBattlePokeCreate_Replace("Poke_TurnCheck", 0x30);
        setBattlePokeCreate_Replace("Poke_ClearTurnFlag", 0x28);
        setBattlePokeCreate_Replace("Poke_ClearForFainted", 0x80, 0x0A);
        setBattlePokeCreate_Replace("Poke_ClearForSwitchOut", 0x88, 0x0A);
        setBattlePokeCreate_Replace("Poke_TransformSet", 0xFC, 0xA4);
    }

    private void setBattlePokeCreate_Replace(String label, int wordOffset) {
        setBattlePokeCreate_Replace(label, wordOffset, -1);
    }

    private void setBattlePokeCreate_Replace(String label, int wordOffset, int clearOffset) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int turnFlagOffset = 0x01F8;

        int romAddress = globalAddressMap.getRomAddress(battleOvl, label);
        battleOvl.writeWord(romAddress + wordOffset, turnFlagOffset, false);

        if (clearOffset > -1) {
            battleOvl.writeByte(romAddress + clearOffset, 4);
        }
    }

    public void setMaxSpeedFix() {
        // There's a bug where the max effective speed stat is intended to be 10000, but it's only stored at a maximum of 8191 (0x1FFF), leaving room for overflows
        // We fix this by instead setting the limit to just be 8191 instead of 10000.

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int calculateSpeedRomAddress = globalAddressMap.getRomAddress(battleOvl, "ServerEvent_CalculateSpeed");
        battleOvl.writeWord(calculateSpeedRomAddress + 0xBC, 0x1FFF, false);
    }

    public void setDynamicTurnOrder() {
        if (!hackMode.dynamicTurnOrder)
            return;

        // _I___XXA AAPPPPPP BBBSSSSS SSSSSSSS
        // X = Special (Quash=0, Default=1, Interrupt=2)
        // A = Action (Fight=0, Shift=0, Skip=0, Rotate=1, Item=2, Switch=3, Run=4, Null=4)
        // P = Priority 
        // B = Bracket (Stall=0, Default=1, QuickClaw/Custap=2)
        // S = Speed

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        // This sets the default action order data to include the Quash flag
        List<String> generateActionOrderLines = readLines("battle/serverflow/generate_action_order.s");
        battleOvl.writeCodeForceInline(generateActionOrderLines, "ServerFlow_GenerateActionOrder", true);

        // Used by Quash; sets the Quash flag to 0 (default is 1)
        List<String> sendToLastLines = readLines("battle/serverflow/action_order_tool_send_to_last.s");
        battleOvl.writeCodeForceInline(sendToLastLines, "ActionOrderTool_SendToLast", true);

        // Used by Round, Pledge Moves, After You, etc. to force the next move despite turn order by setting the interrupt flag to 1
        List<String> interruptLines = readLines("battle/serverflow/action_order_tool_interrupt.s");
        battleOvl.writeCodeForceInline(interruptLines, "ActionOrderTool_Interrupt", true);

        // Normally, the game only updates and sorts the next actions if the last action was a Rotate while the current action is not.
        // We can instead force it to update and sort after every action. This only updates the move priority and recalculates speed.
        // However, we still need to ensure that the special priority is only updated in the aforementioned case and that we're only doing
        // this new update and sort if it's the "Fight" action type. This prevents resorting during phases outside the main move-using phase.
        List<String> actOrderProcMainLines = readLines("battle/serverflow/act_order_proc_main.s");
        battleOvl.writeCodeForceInline(actOrderProcMainLines, "ServerFlow_ActOrderProcMain", true);

        // We add a new boolean parameter representing whether we should also update the special priority (Quick Claw, Custap Berry, Stall) or not.
        List<String> updateActionPriorityLines = readLines("battle/serverflow/update_action_priority.s");
        battleOvl.writeCodeForceInline(updateActionPriorityLines, "ServerFlow_UpdateActionPriority", true);
    }

    public void setTerrains() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int fieldInitAddress = globalAddressMap.getRomAddress(battleOvl, "Field_Init");
        int fieldWorkAddress = battleOvl.readWord(fieldInitAddress + 0x08);
        armParser.addGlobalValue("BATTLE_FIELD_WORK", fieldWorkAddress);

        List<String> initCoreLines = readLines("battle/field/init_core.s");
        battleOvl.writeCodeForceInline(initCoreLines, "Field_InitCore", false);


        // FIELD WEATHER
        // Updates all weather functions to use the modified data structure

        List<String> getWeatherCoreLines = readLines("battle/field/get_weather_core.s");
        battleOvl.writeCodeForceInline(getWeatherCoreLines, "Field_GetWeatherCore", true);

        List<String> getWeatherTurnsCoreLines = readLines("battle/field/get_weather_turns_core.s");
        battleOvl.writeCodeForceInline(getWeatherTurnsCoreLines, "Field_GetWeatherTurnsCore", true);

        List<String> setWeatherCoreLines = readLines("battle/field/set_weather_core.s");
        battleOvl.writeCodeForceInline(setWeatherCoreLines, "Field_SetWeatherCore", true);

        List<String> endWeatherLines = readLines("battle/field/end_weather_core.s");
        battleOvl.writeCodeForceInline(endWeatherLines, "Field_EndWeatherCore", true);

        List<String> turnCheckWeatherCoreLines = readLines("battle/field/turn_check_weather_core.s");
        battleOvl.writeCodeForceInline(turnCheckWeatherCoreLines, "Field_TurnCheckWeatherCore", true);


        // FIELD TERRAIN
        // Adds new functions for terrains that mirror the functions for weather

        List<String> getTerrainCoreLines = readLines("battle/field/get_terrain_core.s");
        battleOvl.writeCode(getTerrainCoreLines, "Field_GetTerrainCore", true);

        List<String> getTerrainTurnsCoreLines = readLines("battle/field/get_terrain_turns_core.s");
        battleOvl.writeCode(getTerrainTurnsCoreLines, "Field_GetTerrainTurnsCore", true);

        List<String> setTerrainCoreLines = readLines("battle/field/set_terrain_core.s");
        battleOvl.writeCode(setTerrainCoreLines, "Field_SetTerrainCore", true);

        List<String> endTerrainLines = readLines("battle/field/end_terrain.s");
        battleOvl.writeCode(endTerrainLines, "Field_EndTerrain", true);

        List<String> turnCheckTerrainCoreLines = readLines("battle/field/turn_check_terrain_core.s");
        battleOvl.writeCode(turnCheckTerrainCoreLines, "Field_TurnCheckTerrainCore", true);

        List<String> getTerrainLines = readLines("battle/field/get_terrain.s");
        battleOvl.writeCode(getTerrainLines, "Field_GetTerrain", true);

        List<String> getTerrainTurnsLines = readLines("battle/field/get_terrain_turns.s");
        battleOvl.writeCode(getTerrainTurnsLines, "Field_GetTerrainTurns", true);

        List<String> setTerrainLines = readLines("battle/field/set_terrain.s");
        battleOvl.writeCode(setTerrainLines, "Field_SetTerrain", true);

        List<String> turnCheckTerrainLines = readLines("battle/field/turn_check_terrain.s");
        battleOvl.writeCode(turnCheckTerrainLines, "Field_TurnCheckTerrain", true);


        // POKE

        List<String> pokeCalcTerrainDamageLines = readLines("battle/poke/calc_terrain_damage.s");
        battleOvl.writeCode(pokeCalcTerrainDamageLines, "Poke_CalcTerrainDamage", true);

        List<String> pokeCalcTerrainHealLines = readLines("battle/poke/calc_terrain_heal.s");
        battleOvl.writeCode(pokeCalcTerrainHealLines, "Poke_CalcTerrainHeal", true);


        // SERVER DISPLAY

        List<String> serverDisplayTerrainDamageLines = readLines("battle/serverdisplay/terrain_damage.s");
        battleOvl.writeCode(serverDisplayTerrainDamageLines, "ServerDisplay_TerrainDamage", true);

        List<String> serverDisplayTerrainHealLines = readLines("battle/serverdisplay/terrain_heal.s");
        battleOvl.writeCode(serverDisplayTerrainHealLines, "ServerDisplay_TerrainHeal", true);


        // SERVER EVENT

        List<String> serverEventPostChangeTerrainLines = readLines("battle/serverevent/post_change_terrain.s");
        battleOvl.writeCode(serverEventPostChangeTerrainLines, "ServerEvent_PostChangeTerrain", true);

        List<String> serverEventCheckTerrainDamageReactionLines = readLines("battle/serverevent/check_terrain_damage_reaction.s");
        battleOvl.writeCode(serverEventCheckTerrainDamageReactionLines, "ServerEvent_CheckTerrainDamageReaction", true);

        List<String> serverEventCheckTerrainHealReactionLines = readLines("battle/serverevent/check_terrain_heal_reaction.s");
        battleOvl.writeCode(serverEventCheckTerrainHealReactionLines, "ServerEvent_CheckTerrainHealReaction", true);


        // SERVER CONTROL

        List<String> serverControlGetTerrainLines = readLines("battle/servercontrol/get_terrain.s");
        battleOvl.writeCode(serverControlGetTerrainLines, "ServerControl_GetTerrain", true);

        // Refs: Field_GetTerrain, Field_GetTerrainTurns
        List<String> serverControlChangeTerrainCheckLines = readLines("battle/servercontrol/change_terrain_check.s");
        battleOvl.writeCode(serverControlChangeTerrainCheckLines, "ServerControl_ChangeTerrainCheck", true);

        // Refs: ServerEvent_PostChangeTerrain
        List<String> serverControlPostChangeTerrainLines = readLines("battle/servercontrol/post_change_terrain.s");
        battleOvl.writeCode(serverControlPostChangeTerrainLines, "ServerControl_PostChangeTerrain", true);

        // Refs: Field_SetTerrain, ServerControl_PostChangeTerrain
        List<String> serverControlChangeTerrainCoreLines = readLines("battle/servercontrol/change_terrain_core.s");
        battleOvl.writeCode(serverControlChangeTerrainCoreLines, "ServerControl_ChangeTerrainCore", true);

        // Refs: Field_TurnCheckTerrain, Poke_CalcTerrainDamage, Poke_CalcTerrainHeal, ServerEvent_CheckTerrainDamageReaction, ServerEvent_CheckTerrainHealReaction,
        //       ServerDisplay_TerrainDamage, ServerDisplay_TerrainHeal, ServerControl_PostChangeTerrain
        List<String> serverControlTurnCheckTerrainLines = readLines("battle/servercontrol/turn_check_terrain.s");
        battleOvl.writeCode(serverControlTurnCheckTerrainLines, "ServerControl_TurnCheck_Terrain", true);

        // Refs: ServerControl_TurnCheck_Terrain
        List<String> serverControlTurnCheckLines = readLines("battle/servercontrol/turn_check.s");
        battleOvl.writeCodeForceInline(serverControlTurnCheckLines, "ServerControl_TurnCheck", true);


        // EVENT HANDLER
        // Refs: ServerControl_ChangeTerrainCheck, ServerControl_ChangeTerrainCore
        List<String> handlerChangeTerrainLines = readLines("battle/serverflow/handler_change_terrain.s");
        battleOvl.writeCode(handlerChangeTerrainLines, "Handler_ChangeTerrain", true);

        // Refs: Handler_ChangeTerrain
        List<String> serverFlowHandlerExecuteLines = readLines("battle/serverflow/handler_execute.s");
        battleOvl.writeCodeForceInline(serverFlowHandlerExecuteLines, "Handler_Execute", true);
    }

    public void setPokemonData() {
        // Update Classic
        updatePokemonBaseData(classicPokes);
        setPokemonDataFromIni(classicPokes, null, "gen9pokes.ini", new double[]{0.2, 0.35, 0.5});

        if (hackMode.pokemonData == null)
            return;

        if (hackMode.pokemonData instanceof HackMode.PokemonDataIni pokemonDataIni) {
            if (pokemonDataIni.fromModern)
                updatePokemonBaseData(pokes);

            setPokemonDataFromIni(pokes, pokeUpdates, pokemonDataIni.filename, hackMode.pokemonDataExpYieldScale);
            return;
        }

        if (hackMode.pokemonData instanceof HackMode.PokemonDataNarc pokemonDataNarc) {
            setPokemonDataFromNarcs(pokemonDataNarc.personal, pokemonDataNarc.levelUpMoves, pokemonDataNarc.evolutions);
            return;
        }

        throw new RuntimeException();
    }

    private void updatePokemonBaseData(Pokemon[] pokemon) {
        for (Pokemon poke : pokemon) {
            if (poke == null)
                continue;

            // Timburr and Stunfisk are the only two Pokémon exempt from the 70 -> 50 change in Gen VIII
            if (poke.baseFriendship == 70
                    && poke.number != Species.timburr
                    && poke.number != Species.stunfisk)
                poke.baseFriendship = 50;

            // Blissey and Slaking are the only two 3rd stage Pokémon with altered exp yields
            // All other Pokémon with altered exp yields did not receive stat changes in later generations
            if (poke.stage == 3
                    && poke.number != Species.blissey
                    && poke.number != Species.slaking)
                poke.expYield = (int) Math.round(poke.bst() * 0.5);
        }
    }

    private void setPokemonDataFromIni(Pokemon[] pokes, PokeUpdate[] pokeUpdates, String filename, double[] stageExpMultipliers) {
        String extension = ".ini";
        if (!filename.endsWith(extension))
            filename += extension;

        Scanner sc;
        try {
            sc = new Scanner(FileFunctions.openConfig("paragonlite/" + filename), StandardCharsets.UTF_8);
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
                String nameStr = q.substring(5, q.length() - 1);

                int num = Integer.parseInt(numStr);
                poke = pokes[num];
                int formeSuffixOffset = nameStr.lastIndexOf('-');
                if (formeSuffixOffset >= nameStr.length() - 3) {
                    String formeSuffix = nameStr.substring(formeSuffixOffset);

                    int pokeNum = Gen5Constants.getFormeBySuffix(num, formeSuffix);
                    poke = pokes[pokeNum];
                }

                pokeUpdate = pokeUpdates != null ? pokeUpdates[num] : null;
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

                    updatePokeType(poke, pokeUpdate, newPrimaryType, newSecondaryType);
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

                    updatePokeBaseStats(poke, pokeUpdate, newHp, newAttack, newDefense, newSpatk, newSpdef, newSpeed);

                    double expMultiplier = stageExpMultipliers[Math.min(poke.stage, stageExpMultipliers.length) - 1];
                    int newExpYield = (int) Math.round(poke.bst() * expMultiplier);
                    updatePokeExpYield(poke, pokeUpdate, newExpYield);

                    // Only consider this modification if this is the intended update and not "Classic"
                    if (pokeUpdate != null)
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

                    updatePokeAbilities(poke, pokeUpdate, newAbility, newAbility, newAbility);

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
                    if (!abilityStrs[1].isEmpty() && newAbility2 == 0) {
                        System.err.println("invalid ability \"" + abilityStrs[1] + "\" on " + poke.name);
                        continue;
                    }

                    int newAbility3 = Math.max(0, abilityNames.indexOf(abilityStrs[2]));
                    if (!abilityStrs[2].isEmpty() && newAbility3 == 0) {
                        System.err.println("invalid ability \"" + abilityStrs[2] + "\" on " + poke.name);
                        continue;
                    }

                    updatePokeAbilities(poke, pokeUpdate, newAbility1, newAbility2, newAbility3);

                    break;
                }
                case "EVYield": {
                    String[] evYieldStrs = value.substring(1, value.length() - 1).split(",", 6);
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

                    updatePokeEVYield(poke, pokeUpdate, newHpEVs, newAttackEVs, newDefenseEVs, newSpatkEVs, newSpdefEVs, newSpeedEVs);

                    break;
                }
                case "ExpYield": {
                    int newExpYield = Integer.parseUnsignedInt(value);
                    updatePokeExpYield(poke, pokeUpdate, newExpYield);
                    break;
                }
                case "CatchRate": {
                    int newCatchRate = Integer.parseUnsignedInt(value);
                    UpdatePokeCatchRate(poke, pokeUpdate, newCatchRate);
                    break;
                }
                case "EggGroups": {
                    int newEggGroup1;
                    int newEggGroup2;

                    if (value.startsWith("[") && value.endsWith("]")) {
                        String[] eggGroupStrs = value.substring(1, value.length() - 1).split(",", 2);
                        newEggGroup1 = EggGroup.valueOf(eggGroupStrs[0].trim().toUpperCase()).ordinal();
                        newEggGroup2 = eggGroupStrs.length == 2 ? EggGroup.valueOf(eggGroupStrs[1].trim().toUpperCase()).ordinal() : -1;
                    } else {
                        newEggGroup1 = EggGroup.valueOf(value.toUpperCase()).ordinal();
                        newEggGroup2 = -1;
                    }

                    updatePokeEggGroups(poke, pokeUpdate, newEggGroup1, newEggGroup2);
                    break;
                }
            }
        }

        System.out.println("Set Pokémon data");
    }

    private void setPokemonDataFromNarcs(String personal, String levelUpMoves, String evolutions) {
        try {
            byte[] levelUpMovesBytes = readBytes(levelUpMoves);
            NARCArchive levelUpMovesNarc = new NARCArchive(levelUpMovesBytes);
            romHandler.writeNARC(romHandler.romEntry.getFile("PokemonMovesets"), levelUpMovesNarc);

            byte[] evolutionsBytes = readBytes(evolutions);
            NARCArchive evolutionsNarc = new NARCArchive(evolutionsBytes);
            romHandler.writeNARC(romHandler.romEntry.getFile("PokemonEvolutions"), evolutionsNarc);

            byte[] personalBytes = readBytes(personal);
            NARCArchive personalNarc = new NARCArchive(personalBytes);

            PokeUpdate pokeUpdate = new PokeUpdate();

            for (int i = 1; i < pokes.length; ++i) {
                Pokemon classicPoke = classicPokes[i];
                Pokemon poke = pokes[i];

                byte[] personalData = personalNarc.files.get(i);

                // Base Stats
                int hp = personalData[Gen4Constants.bsHPOffset] & 0xFF;
                int attack = personalData[Gen4Constants.bsAttackOffset] & 0xFF;
                int defense = personalData[Gen4Constants.bsDefenseOffset] & 0xFF;
                int spatk = personalData[Gen4Constants.bsSpAtkOffset] & 0xFF;
                int spdef = personalData[Gen4Constants.bsSpDefOffset] & 0xFF;
                int speed = personalData[Gen4Constants.bsSpeedOffset] & 0xFF;
                updatePokeBaseStats(poke, pokeUpdate, hp, attack, defense, spatk, spdef, speed);

                // Type
                int primaryType = personalData[Gen5Constants.bsPrimaryTypeOffset] & 0xFF;
                int secondaryType = personalData[Gen5Constants.bsSecondaryTypeOffset] & 0xFF;
                if (primaryType == secondaryType)
                    secondaryType = -1;
                updatePokeType(poke, pokeUpdate, primaryType, secondaryType);

                // Catch Rate
                int catchRate = personalData[Gen5Constants.bsCatchRateOffset] & 0xFF;
                UpdatePokeCatchRate(poke, pokeUpdate, catchRate);
                UpdatePokeCatchRate(classicPoke, null, catchRate);

                // EV Yield
                int hpEVs = personalData[Gen5Constants.bsEVYieldOffset] & 0x03;
                int attackEVs = (personalData[Gen5Constants.bsEVYieldOffset] >> 2) & 0x03;
                int defenseEVs = (personalData[Gen5Constants.bsEVYieldOffset] >> 4) & 0x03;
                int spatkEVs = personalData[Gen5Constants.bsEVYieldOffset + 1] & 0x03;
                int spdefEVs = (personalData[Gen5Constants.bsEVYieldOffset + 1] >> 2) & 0x03;
                int speedEVs = (personalData[Gen5Constants.bsEVYieldOffset] >> 6) & 0x03;
                updatePokeEVYield(poke, pokeUpdate, hpEVs, attackEVs, defenseEVs, spatkEVs, spdefEVs, speedEVs);

                // Grounded Entry
                poke.groundedEntry = (personalData[Gen5Constants.bsEVYieldOffset + 1] >> 4) != 0;
                classicPoke.groundedEntry = poke.groundedEntry;

                // Items
                int item1 = readHalf(personalData, Gen5Constants.bsCommonHeldItemOffset);
                int item2 = readHalf(personalData, Gen5Constants.bsRareHeldItemOffset);
                int item3 = readHalf(personalData, Gen5Constants.bsDarkGrassHeldItemOffset);
                updatePokeItems(poke, pokeUpdate, item1, item2, item3);
                updatePokeItems(classicPoke, null, item1, item2, item3);

                // Gender Ratio
                int genderRatio = personalData[Gen5Constants.bsGenderRatioOffset] & 0xFF;
                updatePokeGenderRatio(poke, pokeUpdate, genderRatio);
                updatePokeGenderRatio(classicPoke, null, genderRatio);

                // Base Friendship
                int baseFriendship = personalData[Gen5Constants.bsBaseFriendshipOffset] & 0xFF;
                updatePokeBaseFriendship(poke, pokeUpdate, baseFriendship);

                // Growth Rate
                int growthRate = personalData[Gen5Constants.bsGrowthRateOffset] & 0x3F;
                updatePokeGrowthRate(poke, pokeUpdate, growthRate);

                // Egg Groups
                int eggGroup1 = personalData[Gen5Constants.bsEggGroup1Offset] & 0x3F;
                int eggGroup2 = personalData[Gen5Constants.bsEggGroup2Offset] & 0x3F;
                updatePokeEggGroups(poke, pokeUpdate, eggGroup1, eggGroup2);

                // Abilities
                int ability1 = (personalData[Gen5Constants.bsAbility1Offset] & 0xFF) | ((personalData[Gen5Constants.bsAbility1Offset - 3] & 0xC0) << 2);
                int ability2 = (personalData[Gen5Constants.bsAbility2Offset] & 0xFF) | ((personalData[Gen5Constants.bsAbility2Offset - 3] & 0xC0) << 2);
                int ability3 = (personalData[Gen5Constants.bsAbility3Offset] & 0xFF) | ((personalData[Gen5Constants.bsAbility3Offset - 3] & 0xC0) << 2);
                updatePokeAbilities(poke, pokeUpdate, ability1, ability2, ability3);
            }

            var oldPokeNarc = romHandler.pokeNarc;
            pokes = romHandler.loadPokemonStats(personalNarc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updatePokeBaseStats(Pokemon poke, PokeUpdate pokeUpdate, int newHp, int newAttack, int newDefense, int newSpAtk, int newSpDef, int newSpeed) {
        if (pokeUpdate != null) {
            pokeUpdate.hp += newHp - poke.hp;
            pokeUpdate.attack += newAttack - poke.attack;
            pokeUpdate.defense += newDefense - poke.defense;
            pokeUpdate.spatk += newSpAtk - poke.spatk;
            pokeUpdate.spdef += newSpDef - poke.spdef;
            pokeUpdate.speed += newSpeed - poke.speed;
        }

        poke.hp = newHp;
        poke.attack = newAttack;
        poke.defense = newDefense;
        poke.spatk = newSpAtk;
        poke.spdef = newSpDef;
        poke.speed = newSpeed;
    }

    private static void updatePokeType(Pokemon poke, PokeUpdate pokeUpdate, int newPrimaryType, int newSecondaryType) {
        if (pokeUpdate != null) {
            pokeUpdate.type1 += newPrimaryType - poke.primaryType.ordinal();
            pokeUpdate.type2 += newSecondaryType - (poke.secondaryType == null ? -1 : poke.secondaryType.ordinal());
        }

        poke.primaryType = Type.values()[newPrimaryType];
        poke.secondaryType = newSecondaryType < 0 ? null : Type.values()[newSecondaryType];
    }

    private static void UpdatePokeCatchRate(Pokemon poke, PokeUpdate pokeUpdate, int newCatchRate) {
        if (pokeUpdate != null)
            pokeUpdate.catchRate += newCatchRate - poke.catchRate;

        poke.catchRate = newCatchRate;
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

        updatePokeEVYield(poke, pokeUpdate, evs[0], evs[1], evs[2], evs[3], evs[4], evs[5]);
    }

    private static void updatePokeEVYield(Pokemon poke, PokeUpdate pokeUpdate, int newHpEVs, int newAttackEVs, int newDefenseEVs, int newSpatkEVs, int newSpdefEVs, int newSpeedEVs) {
        if (pokeUpdate != null) {
            pokeUpdate.hpEVs += newHpEVs - poke.hpEVs;
            pokeUpdate.attackEVs += newAttackEVs - poke.attackEVs;
            pokeUpdate.defenseEVs += newDefenseEVs - poke.defenseEVs;
            pokeUpdate.spatkEVs += newSpatkEVs - poke.spatkEVs;
            pokeUpdate.spdefEVs += newSpdefEVs - poke.spdefEVs;
            pokeUpdate.speedEVs += newSpeedEVs - poke.speedEVs;
        }

        poke.hpEVs = newHpEVs;
        poke.attackEVs = newAttackEVs;
        poke.defenseEVs = newDefenseEVs;
        poke.spatkEVs = newSpatkEVs;
        poke.spdefEVs = newSpdefEVs;
        poke.speedEVs = newSpeedEVs;
    }

    private static void updatePokeItems(Pokemon poke, PokeUpdate pokeUpdate, int newItem1, int newItem2, int newItem3) {
        if (newItem1 == newItem2) {
            if (pokeUpdate != null) {
                pokeUpdate.itemGuaranteed += newItem1 - poke.guaranteedHeldItem;
                pokeUpdate.itemCommon -= poke.commonHeldItem;
                pokeUpdate.itemRare -= poke.rareHeldItem;
                pokeUpdate.itemDarkGrass -= poke.darkGrassHeldItem;
            }

            poke.guaranteedHeldItem = newItem1;
            poke.commonHeldItem = 0;
            poke.rareHeldItem = 0;
            poke.darkGrassHeldItem = 0;
        } else {
            if (pokeUpdate != null) {
                pokeUpdate.itemGuaranteed -= poke.guaranteedHeldItem;
                pokeUpdate.itemCommon += newItem1 - poke.commonHeldItem;
                pokeUpdate.itemRare += newItem2 - poke.rareHeldItem;
                pokeUpdate.itemDarkGrass += newItem3 - poke.darkGrassHeldItem;
            }

            poke.guaranteedHeldItem = 0;
            poke.commonHeldItem = newItem1;
            poke.rareHeldItem = newItem2;
            poke.darkGrassHeldItem = newItem3;
        }
    }

    private static void updatePokeGenderRatio(Pokemon poke, PokeUpdate pokeUpdate, int newGenderRatio) {
        if (pokeUpdate != null)
            pokeUpdate.genderRatio += newGenderRatio - poke.genderRatio;

        poke.genderRatio = newGenderRatio;
    }

    private static void updatePokeBaseFriendship(Pokemon poke, PokeUpdate pokeUpdate, int newBaseFriendship) {
        if (pokeUpdate != null)
            pokeUpdate.baseFriendship += newBaseFriendship - poke.baseFriendship;

        poke.baseFriendship = newBaseFriendship;
    }

    private static void updatePokeGrowthRate(Pokemon poke, PokeUpdate pokeUpdate, int newGrowthRate) {
        if (pokeUpdate != null)
            pokeUpdate.growthRate += newGrowthRate - poke.growthRate.toByte();

        poke.growthRate = ExpCurve.fromByte((byte) newGrowthRate);
    }

    private static void updatePokeEggGroups(Pokemon poke, PokeUpdate pokeUpdate, int newEggGroup1, int newEggGroup2) {
        if (pokeUpdate != null) {
            pokeUpdate.eggGroup1 += newEggGroup1 - poke.eggGroup1.toByte();
            pokeUpdate.eggGroup2 += newEggGroup2 - (poke.eggGroup2 == null ? -1 : poke.eggGroup2.toByte());
        }

        poke.eggGroup1 = EggGroup.fromByte((byte) newEggGroup1);
        poke.eggGroup2 = newEggGroup2 < 0 ? null : EggGroup.fromByte((byte) newEggGroup2);
    }

    private static void updatePokeAbilities(Pokemon poke, PokeUpdate pokeUpdate, int newAbility1, int newAbility2, int newAbility3) {
        if (pokeUpdate != null) {
            pokeUpdate.ability1 += newAbility1 - poke.ability1;
            pokeUpdate.ability2 += newAbility2 - poke.ability2;
            pokeUpdate.ability3 += newAbility3 - poke.ability3;
        }

        poke.ability1 = newAbility1;
        poke.ability2 = newAbility2;
        poke.ability3 = newAbility3;
    }

    private static void updatePokeExpYield(Pokemon poke, PokeUpdate pokeUpdate, int newExpYield) {
        if (pokeUpdate != null)
            pokeUpdate.expYield += newExpYield - poke.expYield;

        poke.expYield = newExpYield;
    }

    public void setBoxSearchAbilities(Settings settings) {
        // If we're going to be randomizing, we use all possible abilities
        if (settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE) {
            setBoxSearchAbilitiesInternal(romHandler.getAvailableAbilities(settings));
            return;
        }

        // Only include abilities used by at least one pokemon species
        Set<Integer> uniqueAbilities = new HashSet<>();
        for (Pokemon pk : pokes) {
            uniqueAbilities.add(pk.ability1);
            uniqueAbilities.add(pk.ability2);
            uniqueAbilities.add(pk.ability3);
        }
        setBoxSearchAbilitiesInternal(uniqueAbilities);
    }

    private void setBoxSearchAbilitiesInternal(Set<Integer> uniqueAbilities) {
        // TODO: Localization
        Collator collator = Collator.getInstance(Locale.US);
        int lettersInAlphabet = 26;

        uniqueAbilities.remove(0); // removes null ability

        List<Integer> abilities = new ArrayList<>(uniqueAbilities);
        List<String> tempAbilityNames = new ArrayList<>(abilities.size());
        Map<String, Integer> abilityNameToId = new HashMap<>(abilities.size());
        for (int abilityId : abilities) {
            String abilityName = abilityNames.get(abilityId);

            tempAbilityNames.add(abilityName);
            abilityNameToId.put(abilityName, abilityId);
        }

        List<String> sortedAbilityNames = tempAbilityNames;
        sortedAbilityNames.sort(collator);

        List<Integer> sortedAbilityIds = new ArrayList<>(abilities.size());
        for (String abilityName : sortedAbilityNames) {
            int abilityId = abilityNameToId.get(abilityName);
            sortedAbilityIds.add(abilityId);
        }

        byte[] sortedAbilityIdsData = new byte[sortedAbilityIds.size() * 2];
        for (int i = 0; i < sortedAbilityIds.size(); ++i)
            writeHalf(sortedAbilityIdsData, i * 2, sortedAbilityIds.get(i));

        // Data_SearchAbilityLetterOffsets
        int[] numPerLetter = new int[lettersInAlphabet];
        char startingInitial = 'A';
        char currentInitial = startingInitial;
        int count = 1;
        for (int i = 0; i < sortedAbilityNames.size(); ++i) {
            String abilityName = sortedAbilityNames.get(i);
            char abilityInitial = abilityName.charAt(0);
            if (abilityInitial != currentInitial) {
                currentInitial = abilityInitial;
                count = 1;
            }

            int currentCount = numPerLetter[currentInitial - startingInitial];
        }

        int letterOffset = 1;
        byte[] letterOffsetsData = new byte[lettersInAlphabet * 4];
        for (int i = 0; i < lettersInAlphabet; ++i) {
            writeHalf(letterOffsetsData, i * 4, letterOffset);
            writeHalf(letterOffsetsData, i * 4 + 2, letterOffset);
        }

        // Data_SearchAbilityNameSort
        ParagonLiteOverlay storageSystemOvl = overlays.get(OverlayId.STORAGE_SYSTEM);
        ParagonLiteAddressMap.DataAddress nameSortDataAddress = storageSystemOvl.replaceData(sortedAbilityIdsData, "Data_SearchAbilityNameSort");
        int nameSortDataRomAddress = nameSortDataAddress.getRamAddress();

        int searchCodeRomAddress = globalAddressMap.getRomAddress(storageSystemOvl, "Search_Core");
        storageSystemOvl.writeWord(searchCodeRomAddress + 0x30A, nameSortDataRomAddress, true);


    }

    public void setTypeEffectiveness() {

    }

    public void setTrainerAI() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        ParagonLiteOverlay trainerAIOvl = overlays.get(OverlayId.TRAINER_AI);

//        // TODO: This is used for the multiplicative version of move selection
//        trainerAIOvl.writeByte(0x0217F842, 4096);

//        writeTrainerAIFile(trainerAIScriptsNarc, 14); // Test

        int battleClientLineNumRomAddress = globalAddressMap.getRomAddress(battleOvl, "Data_BattleClient_Init_LineNum");
        int battleClientLineNum = battleOvl.readWord(battleClientLineNumRomAddress);

        int battleClientAllocModifyRomAddress = globalAddressMap.getRomAddress(battleOvl, "Inst_BattleClient_Init_AllocModify");
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

        int scriptCommandTableRomAddress = globalAddressMap.getRomAddress(trainerAIOvl, "Data_AIScriptCommands");

        // New OP: Multiply Score (0x2B)
        List<String> multiplyScoreLines = readLines("trainerai/scripts/multiply_score.s");
        int multiplyScoreAddress = trainerAIOvl.writeCode(multiplyScoreLines, "MultiplyScore", true);
        trainerAIOvl.writeWord(scriptCommandTableRomAddress + (0x2B * 4), multiplyScoreAddress + 1, true);

        // New OP: Multiply Score by Stored (0x3C)
        List<String> multiplyScoreByStoredLines = readLines("trainerai/scripts/multiply_score_by_stored.s");
        int multiplyScoreByStoredAddress = trainerAIOvl.writeCode(multiplyScoreByStoredLines, "MultiplyScoreByStored", true);
        trainerAIOvl.writeWord(scriptCommandTableRomAddress + (0x3C * 4), multiplyScoreByStoredAddress + 1, true);

        // New OP: Get Simulation Multiplier (0x3E)
        List<String> getSimulationMultiplierLines = readLines("trainerai/scripts/get_simulation_multiplier.s");
        int getDamageAddress = trainerAIOvl.writeCode(getSimulationMultiplierLines, "GetSimulationMultiplier", true);
        trainerAIOvl.writeWord(scriptCommandTableRomAddress + (0x3E * 4), getDamageAddress + 1, true);

        System.out.println("Set trainer AI scripts");
    }

    public void setTrainerData() {
        // TrainerPoke_GetGenderAbilityByte
        arm9.writeCode(readLines("arm9/tr_tool/poke_get_gender_ability_byte.s"), "TrainerPoke_GetGenderAbilityByte", true);

        // TrTool_GetParam
        // Refs: TrainerPoke_GetGenderAbilityByte
        arm9.writeCodeForceInline(readLines("arm9/tr_tool/get_param.s"), "TrTool_GetParam", false);

        // TrTool_GetPokeDataSize
        arm9.writeCode(readLines("arm9/tr_tool/get_poke_data_size.s"), "TrTool_GetPokeDataSize", true);

        // TrTool_GetPokeFileSize
        // Refs: TrTool_GetPokeDataSize
        arm9.writeCode(readLines("arm9/tr_tool/get_poke_file_size.s"), "TrTool_GetPokeFileSize", true);

        // TrTool_IsPooled
        arm9.writeCode(readLines("arm9/tr_tool/is_pooled.s"), "TrTool_IsPooled", true);

        // TrTool_PokesHaveStatModifiers
        arm9.writeCode(readLines("arm9/tr_tool/pokes_have_stat_modifiers.s"), "TrTool_PokesHaveStatModifiers", true);

        // TrTool_PokesHaveItems
        arm9.writeCode(readLines("arm9/tr_tool/pokes_have_items.s"), "TrTool_PokesHaveItems", true);

        // TrTool_PokesHaveMoves
        arm9.writeCode(readLines("arm9/tr_tool/pokes_have_moves.s"), "TrTool_PokesHaveMoves", true);

        // TrTool_PokesHaveNatures
        arm9.writeCode(readLines("arm9/tr_tool/pokes_have_natures.s"), "TrTool_PokesHaveNatures", true);

        // TrainerPoke_GetFormId
        arm9.writeCode(readLines("arm9/tr_tool/poke_get_form_id.s"), "TrainerPoke_GetFormId", true);

        // TrTool_MakePokeFromData
        // Refs: TrainerPoke_GetGenderAbilityByte, TrTool_PokesHaveNatures, TrainerPoke_GetFormId
        arm9.writeCode(readLines("arm9/tr_tool/loadparty/make_poke_from_data.s"), "TrTool_MakePokeFromData", true);

        // TrTool_LoadParty_Standard
        // Refs: TrTool_PokesHaveStatModifiers, TrTool_PokesHaveItems, TrTool_PokesHaveMoves, TrTool_MakePokeFromData
        arm9.writeCode(readLines("arm9/tr_tool/loadparty/standard.s"), "TrTool_LoadParty_Standard", true);

        // TrTool_LoadParty_Pooled
        arm9.writeCode(readLines("arm9/tr_tool/loadparty/pooled.s"), "TrTool_LoadParty_Pooled", true);

        // TrTool_LoadParty_Core
        // Refs: TrTool_GetPokeDataSize, TrTool_IsPooled, TrTool_LoadParty_Standard, TrTool_LoadParty_Pooled
        // we are ultimately replacing TrTool_LoadParty with this, but we add separately for naming purposes
        arm9.writeCode(readLines("arm9/tr_tool/loadparty/core.s"), "TrTool_LoadParty_Core", true);

        // BattleSetup_LoadTrainer
        // Refs: TrTool_LoadParty_Core
        arm9.writeCodeForceInline(readLines("arm9/btl_setup/load_trainer.s"), "BattleSetup_LoadTrainer", !debugMode);

        if (romHandler.isUpperVersion()) {
            ParagonLiteOverlay pwtBattleOvl = overlays.get(OverlayId.PWT_BATTLE);

            // TrainerPartySetup
            // Refs: TrTool_LoadParty_Core
            pwtBattleOvl.writeCodeForceInline(readLines("pwtbattle/trainer_party_setup.s"), "TrainerPartySetup", true);
        }

        // Update minor changes to TrTool_LoadTrainer
        {
            int loadTrainerFuncAddress = globalAddressMap.getRomAddress(arm9, "TrTool_LoadTrainer");
            arm9.writeHalfword(loadTrainerFuncAddress + 0x28, 0x78B1); // ldrb r1, [r6, #TrainerData.class]

            // No need to update items or AI flags here as they're in the same position
        }

        if (hackMode.trainerData == null)
            return;

        byte[] trainerDataBytes = readBytes(hackMode.trainerData.dataNarc);
        byte[] trainerPokeBytes = readBytes(hackMode.trainerData.pokeNarc);
        try {
            NARCArchive trainerDataNarc = new NARCArchive(trainerDataBytes);
            NARCArchive trainerPokeNarc = new NARCArchive(trainerPokeBytes);

            var trainers = romHandler.getTrainers(trainerDataNarc, trainerPokeNarc);
            romHandler.setTrainers(trainers, false, false, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Set trainers");
    }

    public void setAbilities() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        ParagonLiteOverlay battleServerOvl = overlays.get(OverlayId.BATTLE_SERVER);

        registerAbilityEffects();

        // Changes Blaze/Torrent/Overgrow/Swarm
        setLowHpTypeBoostAbility();

        // Changes Drizzle/Drought/Sand Stream/Snow Warning 
        setCommonWeatherChangeAbility();

        // Move-type-changing abilities
        battleOvl.writeCode(readLines("eventhandlers/ability/common_move_type_change_type.s"), "CommonMoveTypeChange_Type", true);
        battleOvl.writeCode(readLines("eventhandlers/ability/common_move_type_change_power.s"), "CommonMoveTypeChange_Power", true);

        // Move-type-changing abilities
        battleOvl.writeCode(readLines("eventhandlers/ability/common_heal_allies.s"), "CommonHealAlliesAbility", true);

        // Default names
        List<String> newText = Arrays.asList(new String[ParagonLiteAbilities.MAX - Gen5Constants.highestAbilityIndex]);
        for (int i = 0; i < newText.size(); ++i)
            newText.set(i, String.format("#%03d", abilityNames.size() + i));
        abilityNames.addAll(newText);

        // Default descriptions
        Collections.fill(newText, " -");
        abilityDescriptions.addAll(newText);

        // Default Cheren explanations
        if (romHandler.isUpperVersion()) {
            String eggExplanation = abilityExplanations.remove(abilityExplanations.size() - 1);

            Collections.fill(newText, "");
            abilityExplanations.addAll(newText);

            abilityExplanations.add(eggExplanation);
        }

        System.out.println("Set abilities");
    }

    private void setLeafGuard() {
        int number = Abilities.leafGuard;

        switch (hackMode.name) {
            case "paragonlite" -> {
                // Description
                String description = "Reduces damage in\\xFFFEsunny weather.";
                abilityDescriptions.set(number, description);

                // Data
                setAbilityEventHandlers(number, new AbilityEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, "leaf_guard.s"));
            }
            case "Redux" -> {
                setAbilityEventHandlers(number,
                        new AbilityEventHandler(Gen5BattleEventType.onAddConditionCheckFail),
                        new AbilityEventHandler(Gen5BattleEventType.onAddConditionFail),
                        new AbilityEventHandler(Gen5BattleEventType.onCheckSleep),
                        new AbilityEventHandler(Gen5BattleEventType.onGetIsCriticalHit, Abilities.battleArmor));
            }
            default -> throw new IllegalStateException("Unexpected value: " + hackMode);
        }
    }

    private void setSlowStart() {
        int number = Abilities.slowStart;

        setAbilityEventHandlers(number,
                new AbilityEventHandler(Gen5BattleEventType.onSwitchIn),
                new AbilityEventHandler(Gen5BattleEventType.onRotateIn),
                new AbilityEventHandler(Gen5BattleEventType.onPostAbilityChange),
                new AbilityEventHandler(Gen5BattleEventType.onCalcSpeed),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStatValue),
                new AbilityEventHandler(Gen5BattleEventType.onTurnCheckEnd, "slow_start_end_of_turn.s"));
    }

    private void setLowHpTypeBoostAbility() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> lines = readLines("eventhandlers/ability/low_hp_type_boost.s");
        battleOvl.writeCodeForceInline(lines, "CommonLowHPTypeBoostAbility", false);
    }

    private void setCommonWeatherChangeAbility() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        List<String> commonWeatherChangeAbilityLines = readLines("eventhandlers/ability/common_weather_change_ability.s");
        battleOvl.writeCodeForceInline(commonWeatherChangeAbilityLines, "CommonWeatherChangeAbility", false);
    }

    public void setMoves() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        ParagonLiteOverlay battleLevelOvl = overlays.get(OverlayId.BATTLE_LEVEL);

        registerMoveEffects();

//         HACK: We have to allocate to Battle overlay and jump to that because we can't allocate new space in BattleLevel
        List<String> btlvEffVMLoadScriptLines = readLines("battlelevel/btlveffvm_load_script.s");
        battleOvl.writeCode(btlvEffVMLoadScriptLines, "BtlvEffVM_LoadScript", true);

        List<String> btlvEffVMLoadScriptJumpLines = readLines("battlelevel/btlveffvm_load_script_jump.s");
        battleLevelOvl.writeCodeForceInline(btlvEffVMLoadScriptJumpLines, "BtlvEffVM_LoadScript", true);

        List<String> playMoveAnimationLines = readLines("battlelevel/play_move_animation.s");
        battleOvl.writeCode(playMoveAnimationLines, "PlayMoveAnimation", true);

        List<String> playMoveAnimationJumpLines = readLines("battlelevel/play_move_animation_jump.s");
        battleLevelOvl.writeCodeForceInline(playMoveAnimationJumpLines, "PlayMoveAnimation", true);

        int maxMoveIndex = Moves.malignantChain;
        int highMoveOffset = 116;
        int battleAnimationScriptsOffset = 561;

        int maxMoveScriptIndex = maxMoveIndex + highMoveOffset - battleAnimationScriptsOffset;

        Map<String, Integer> auxiliaryAnimationScriptIndices = new HashMap<>();
        int auxiliaryAnimationIndex = maxMoveScriptIndex;
        auxiliaryAnimationScriptIndices.put("vise_grip_trap", ++auxiliaryAnimationIndex);
        auxiliaryAnimationScriptIndices.put("infestation_trap", ++auxiliaryAnimationIndex);
        auxiliaryAnimationScriptIndices.put("salt_cure_effect", ++auxiliaryAnimationIndex);

        while (battleAnimationScriptsNarc.files.size() < maxMoveScriptIndex)
            battleAnimationScriptsNarc.files.add(getDefaultAnimationScript());

        // 12
//        arm9.writeByte(0x0215339C, 0x24);

        // 36
//        arm9.writeByte(0x021AC538, 0x24);
//        arm9.writeByte(0x021ADB32, 0x24);
//        arm9.writeByte(0x021ADB56, 0x24);
//        arm9.writeByte(0x021ADB7A, 0x24)

        switch (hackMode.name) {
            case "paragonlite" -> loadMovesFromFile();
            case "Redux" -> {
                loadReduxMoves();
                loadReduxMoveTexts();
            }
            default -> throw new IllegalStateException("Unexpected value: " + hackMode);
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

        // Dance Moves
        for (int moveIndex : new int[]{
                Moves.swordsDance, // 014
                Moves.petalDance, // 080
                Moves.featherDance, // 297
                Moves.teeterDance, // 298
                Moves.dragonDance, // 349
                Moves.lunarDance, // 461
                Moves.quiverDance, // 483
                Moves.fieryDance, // 552
                Moves.revelationDance, // 686
                Moves.clangorousSoul, // 775
                Moves.victoryDance, // 837
                Moves.aquaStep, // 872
        }) {
            moves.get(moveIndex).isCustomDanceMove = true;
        }

        // Roll/Spin Moves
        for (int moveIndex : new int[]{
                Moves.rollingKick, // 027
                Moves.fireSpin, // 083
                Moves.tripleKick, // 167
                Moves.flameWheel, // 172
                Moves.rollout, // 205
                Moves.rapidSpin, // 229
                Moves.iceBall, // 301
                Moves.gyroBall, // 360
                Moves.steamroller, // 537
                Moves.darkestLariat, // 663
                Moves.tripleAxel, // 813
                Moves.spinOut, // 859
                Moves.iceSpinner, // 861
                Moves.mortalSpin, // 866
        }) {
            moves.get(moveIndex).isCustomRollSpinMove = true;
        }


        int[] newMoves;
        int[] movesToClear;
        switch (hackMode.name) {
            case "ParagonLite" -> {
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
                        Moves.belch, // #562
                        Moves.stickyWeb, // #564
                        Moves.fellStinger, // #565
                        Moves.freezeDry, // #573
                        Moves.diamondStorm, // #591
                        Moves.spikyShield, // #596
                        Moves.firstImpression, // #660
                        Moves.darkestLariat, // #663
                        Moves.sparklingAria, // #664
                        Moves.solarBlade, // #669
                        Moves.pollenPuff, // #676
                        Moves.powerTrip, // #681
                        Moves.revelationDance, // #686
                        Moves.auroraVeil, // #694
                        Moves.psychicFangs, // #706
                        Moves.bodyPress, // #776
                        Moves.steelBeam, // #796
                        Moves.scaleShot, // #799
                        Moves.meteorBeam, // #800
                        Moves.poltergeist, // #809
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
            case "Redux" -> {
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
                        Moves.belch, // #562
                        Moves.stickyWeb, // #564
                        Moves.fellStinger, // #565
                        Moves.freezeDry, // #573
                        Moves.diamondStorm, // #591
                        Moves.spikyShield, // #596
                        Moves.firstImpression, // #660
                        Moves.darkestLariat, // #663
                        Moves.sparklingAria, // #664
                        Moves.solarBlade, // #669
                        Moves.pollenPuff, // #676
                        Moves.powerTrip, // #681
                        Moves.revelationDance, // #686
                        Moves.auroraVeil, // #694
                        Moves.psychicFangs, // #706
                        Moves.bodyPress, // #776
                        Moves.steelBeam, // #796
                        Moves.scaleShot, // #799
                        Moves.poltergeist, // #809
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
            default -> throw new IllegalStateException("Unexpected value: " + hackMode);
        }

        int totalMoves = Moves.MAX;
        int moveDataSize = 36;
        arm9.replaceData(new byte[moveDataSize * totalMoves], "Data_MoveCache");

        for (int moveToClear : movesToClear) {
            clearMoveEventHandlers(moveToClear);
        }

        // TODO: For some reason the relocator addresses values are wrong
        relocateMoveListRamAddress(newMoves.length - movesToClear.length);

        // #560 Flying Press
        setMoveAnimations(Moves.flyingPress);

        // + #562 Belch
        setMoveEventHandlers(Moves.belch, new MoveEventHandler(Gen5BattleEventType.onMoveExecuteCheck2, "belch.s"));
        setMoveAnimations(Moves.belch);

        // TODO #564 Sticky Web
        setMoveEventHandlers(Moves.stickyWeb, new MoveEventHandler(Gen5BattleEventType.onUncategorizedMoveNoTarget, "sticky_web.s"));
        setMoveAnimations(Moves.stickyWeb, 770);

        // + #565 Fell Stinger
        setMoveEventHandlers(Moves.fellStinger, new MoveEventHandler(Gen5BattleEventType.onDamageProcessingEnd_HitReal, "fell_stinger.s"));
        setMoveAnimations(Moves.fellStinger);

        // #570 Parabolic Charge
        setMoveAnimations(Moves.parabolicCharge);

        // #572 Petal Blizzard
        setMoveAnimations(Moves.petalBlizzard, 786);

        // + #573 Freeze-Dry
        setMoveEventHandlers(Moves.freezeDry, new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "freeze-dry.s"));
        setMoveAnimations(Moves.freezeDry);

        // #574 Disarming Voice
        setMoveAnimations(Moves.disarmingVoice, 743);

        // #577 Draining Kiss
        setMoveAnimations(Moves.drainingKiss, 744);

        // #580 Grassy Terrain
        setMoveAnimations(Moves.grassyTerrain);

        // #583 Play Rough
        setMoveAnimations(Moves.playRough, 740);

        // #584 Fairy Wind
        setMoveAnimations(Moves.fairyWind);

        // #585 Moonblast
        setMoveAnimations(Moves.moonblast, 741, 742);

        // #586 Boomburst
        setMoveAnimations(Moves.boomburst, 752);

        // + #591 Diamond Storm
        setMoveEventHandlers(Moves.diamondStorm, new MoveEventHandler(Gen5BattleEventType.OnMoveExecuteEnd, "diamond_storm.s"));
        setMoveAnimations(Moves.diamondStorm);

        // #592 Steam Eruption
        setMoveAnimations(Moves.steamEruption);

        // #594 Water Shuriken
        setMoveAnimations(Moves.waterShuriken, 763);

        // #595 Mystical Fire
        setMoveAnimations(Moves.mysticalFire);

        // TODO: + #596 Spiky Shield
        cloneMoveEventHandlers(Moves.spikyShield, Moves.protect);
        setMoveAnimations(Moves.spikyShield, 772);

        // #598 Eerie Impulse
        setMoveAnimations(Moves.eerieImpulse);

        // #604 Electric Terrain
        setMoveAnimations(Moves.electricTerrain, 794);

        // #605 Dazzling Gleam
        setMoveAnimations(Moves.dazzlingGleam, 745);

        // #609 Nuzzle
        setMoveAnimations(Moves.nuzzle);

        // TODO: + #611 Infestation
        setMoveAnimations(Moves.infestation, 747);
        setMoveAuxiliaryAnimation(Moves.infestation, "trap", auxiliaryAnimationScriptIndices);

        // #612 Power-Up Punch
        setMoveAnimations(Moves.powerUpPunch);

        // + #660 First Impression
        cloneMoveEventHandlers(Moves.firstImpression, Moves.fakeOut);
        setMoveAnimations(Moves.firstImpression, 760);

        // #662 Spirit Shackle
        setMoveAnimations(Moves.spiritShackle, 800);

        // + #663 Darkest Lariat
        cloneMoveEventHandlers(Moves.darkestLariat, Moves.chipAway);
        setMoveAnimations(Moves.darkestLariat);

        // TODO: + #664 Sparkling Aria
        setMoveAnimations(Moves.sparklingAria, 773);

        // #665 Ice Hammer
        setMoveAnimations(Moves.iceHammer);

        // #667 High Horsepower
        setMoveAnimations(Moves.highHorsepower, 739);

        // TODO: #668 Strength Sap
        setMoveAnimations(Moves.strengthSap);

        // + #669 Solar Blade
        cloneMoveEventHandlers(Moves.solarBlade, Moves.solarBeam);
        setMoveAnimations(Moves.solarBlade, 781);

        // #670 Leafage
        setMoveAnimations(Moves.leafage, 780);

        // #675 Throat Chop
        setMoveAnimations(Moves.throatChop, 787);

        // + #676 Pollen Puff
        setMoveEventHandlers(Moves.pollenPuff,
                new MoveEventHandler(Gen5BattleEventType.onCheckDamageToRecover, "pollen_puff_set_mode.s"),
                new MoveEventHandler(Gen5BattleEventType.onApplyDamageToRecover, "pollen_puff_heal.s"));
        setMoveAnimations(Moves.pollenPuff, 769);

        // #679 Lunge
        setMoveAnimations(Moves.lunge, 799);

        // #680 Fire Lash
        setMoveAnimations(Moves.fireLash);

        // + #681 Power Trip
        cloneMoveEventHandlers(Moves.powerTrip, Moves.storedPower);
        setMoveAnimations(Moves.powerTrip);

        // #684 Smart Strike
        setMoveAnimations(Moves.smartStrike, 751);

        // + #686 Revelation Dance
        setMoveEventHandlers(Moves.revelationDance, new MoveEventHandler(Gen5BattleEventType.onGetMoveParam, "revelation_dance.s"));
        setMoveAnimations(Moves.revelationDance);

        // #688 Trop Kick
        setMoveAnimations(Moves.tropKick, 784);

        // #693 Brutal Swing
        setMoveAnimations(Moves.brutalSwing, 774);

        // +#694 Aurora Veil
        setMoveEventHandlers(Moves.auroraVeil, new MoveEventHandler(Gen5BattleEventType.onUncategorizedMoveNoTarget, "aurora_veil.s"));
        setMoveAnimations(Moves.auroraVeil, 767);
        byte[] auroraVeilNscr = readBytes("moveanims/bg/694_aurora_veil.nscr");
        byte[] auroraVeilNcgr = readBytes("moveanims/bg/694_aurora_veil.ncgr");
        byte[] auroraVeilNclr = readBytes("moveanims/bg/694_aurora_veil.nclr");
        byte[] auroraVeilAnimBin = readBytes("moveanims/bg/694_aurora_veil_anim.bin");
        byte[] auroraVeilAnimNclr = readBytes("moveanims/bg/694_aurora_veil_anim.nclr");
        moveBackgroundsNarc.files.set(6, auroraVeilNscr);
        moveBackgroundsNarc.files.set(7, auroraVeilNcgr);
        moveBackgroundsNarc.files.set(8, auroraVeilNclr);
        moveAnimatedBackgroundsNarc.files.add(auroraVeilAnimBin); // 46
        moveAnimatedBackgroundsNarc.files.add(auroraVeilAnimNclr); // 47

        // #705 Fleur Cannon
        setMoveAnimations(Moves.fleurCannon, 761);

        // #706 Psychic Fangs
        setMoveEventHandlers(Moves.psychicFangs,
                new MoveEventHandler(Gen5BattleEventType.onMoveDamageProcessing1, Moves.brickBreak),
                new MoveEventHandler(Gen5BattleEventType.onMoveDamageProcessingEnd, Moves.brickBreak),
                new MoveEventHandler(Gen5BattleEventType.onGetMoveDamage, "common_screen_break.s"));
        setMoveAnimations(Moves.psychicFangs, 748, 798);

        // TODO: +#707 Stomping Tantrum
        setMoveAnimations(Moves.stompingTantrum);

        // #708 Shadow Bone
        setMoveAnimations(Moves.shadowBone, 789);

        // #709 Accelerock
        setMoveAnimations(Moves.accelerock);

        // #710 Liquidation
        setMoveAnimations(Moves.liquidation, 785);

        // TODO: +#746 Jaw Lock
        setMoveAnimations(Moves.jawLock, 788);

        // + #776 Body Pres
        setMoveEventHandlers(Moves.bodyPress, new MoveEventHandler(Gen5BattleEventType.onGetAttackingStat, "body_press.s"));
        setMoveAnimations(Moves.bodyPress, 771);

        // #778 Drum Beating
        setMoveAnimations(Moves.drumBeating, 802);

        // #780 Pyro Ball
        setMoveAnimations(Moves.pyroBall, 803);

        // #784 Breaking Swipe
        setMoveAnimations(Moves.breakingSwipe, 755);

        // #786 Overdrive
        setMoveAnimations(Moves.overdrive);

        // #789 Spirit Break
        setMoveAnimations(Moves.spiritBreak);

        // TODO: +#791 Life Dew
        setMoveAnimations(Moves.lifeDew);

        // #794 Meteor Assault
        setMoveAnimations(Moves.meteorAssault);

        // + #796 Steel Beam
        setMoveEventHandlers(Moves.steelBeam, new MoveEventHandler(Gen5BattleEventType.OnMoveExecuteEnd, "steel_beam.s"));
        setMoveAnimations(Moves.steelBeam, 762);

        // #797 Expanding Force
        setMoveAnimations(Moves.expandingForce, 808);

        // + #799 Scale Shot
        setMoveEventHandlers(Moves.scaleShot, new MoveEventHandler(Gen5BattleEventType.OnMoveExecuteEnd, "scale_shot.s"));
        setMoveAnimations(Moves.scaleShot, 758);

        // + #800 Meteor Beam
        if (hackMode.name.equals("Redux")) {
            setMoveAnimations(Moves.meteorBeam, "Redux");
        } else {
            setMoveEventHandlers(Moves.meteorBeam, new MoveEventHandler(Gen5BattleEventType.onChargeUpStartDone, "meteor_beam.s"));
            setMoveAnimations(Moves.meteorBeam);
        }

        // #802 Misty Explosion
        setMoveAnimations(Moves.mistyExplosion, 805);

        // #803 Grassy Glide
        setMoveAnimations(Moves.grassyGlide, 807);

        // #804 Rising Voltage
        setMoveAnimations(Moves.risingVoltage, 806);

        // #806 Skitter Smack
        setMoveAnimations(Moves.skitterSmack, 783);

        // #807 Burning Jealousy
        setMoveAnimations(Moves.burningJealousy, 782);

        // + #809 Poltergeist
        setMoveEventHandlers(Moves.poltergeist,
                new MoveEventHandler(Gen5BattleEventType.onNoEffectCheck, "poltergeist_check.s"),
                new MoveEventHandler(Gen5BattleEventType.onDamageProcessingStart, "poltergeist_message.s"));
        setMoveAnimations(Moves.poltergeist, 797);

        // #810 Corrosive Gas
        setMoveAnimations(Moves.corrosiveGas);

        // + #812 Flip Turn
        cloneMoveEventHandlers(Moves.flipTurn, Moves.uTurn);
        setMoveAnimations(Moves.flipTurn, 759);

        // + #813 Triple Axel
        switch (hackMode.name) {
            // No longer changes damage
            case "ParagonLite" -> setMoveEventHandlers(Moves.tripleAxel, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
            case "Redux" -> setMoveEventHandlers(Moves.tripleAxel,
                    new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "triple_axel_redux.s"),
                    new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
            default -> throw new IllegalStateException("Unexpected value: " + hackMode);
        }
        setMoveAnimations(Moves.tripleAxel, 754);

        // + #814 Dual Wingbeat
        setMoveEventHandlers(Moves.dualWingbeat, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
        setMoveAnimations(Moves.dualWingbeat, 746);

        // #815 Scorching Sands
        setMoveAnimations(Moves.scorchingSands);

        // #816 Jungle Healing
        setMoveAnimations(Moves.jungleHealing);

        // #817 Wicked Blow
        setMoveAnimations(Moves.wickedBlow);

        // + #818 Surging Strikes
        if (hackMode.name.equalsIgnoreCase("ParagonLite"))
            setMoveEventHandlers(Moves.surgingStrikes, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));
        setMoveAnimations(Moves.surgingStrikes, 766);

        // + #827 Dire Claw
        setMoveEventHandlers(Moves.direClaw, new MoveEventHandler(Gen5BattleEventType.onAddCondition, "dire_claw.s"));
        setMoveAnimations(Moves.direClaw);

        // #828 Psyshield Bash
        setMoveAnimations(Moves.psyshieldBash);

        // TODO: + #830 Stone Axe
        setMoveAnimations(Moves.stoneAxe);

        // #833 Raging Fury
        cloneMoveEventHandlers(Moves.ragingFury, Moves.thrash);
        setMoveAnimations(Moves.ragingFury);

        // #834 Wave Crash
        setMoveAnimations(Moves.waveCrash);

        // #837 Victory Dance
        setMoveAnimations(Moves.victoryDance);

        // #838 Headlong Rush
        setMoveAnimations(Moves.headlongRush);

        // + #839 Barb Barrage
        setMoveEventHandlers(Moves.barbBarrage, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "barb_barrage.s"));
        setMoveAnimations(Moves.barbBarrage, 757);

        // #840 Esper Wing
        setMoveAnimations(Moves.esperWing, 749);

        // + #841 Bitter Malice
        cloneMoveEventHandlers(Moves.bitterMalice, Moves.hex);
        setMoveAnimations(Moves.bitterMalice);

        // #843 Triple Arrows
        setMoveAnimations(Moves.tripleArrows, 801);

        // + #844 Infernal Parade
        cloneMoveEventHandlers(Moves.infernalParade, Moves.hex);
        setMoveAnimations(Moves.infernalParade);

        // TODO: + #845 Ceaseless Edge
        setMoveAnimations(Moves.ceaselessEdge);

        // #855 Lumina Crash
        setMoveAnimations(Moves.luminaCrash);

        // #857 Jet Punch
        setMoveAnimations(Moves.jetPunch);

        // #859 Spin Out
        setMoveAnimations(Moves.spinOut);

        // TODO: + #861 Ice Spinner
        setMoveAnimations(Moves.iceSpinner);

        // #862 Glaive Rush
        setMoveAnimations(Moves.glaiveRush, 779);

        // #864 Salt Cure
        setMoveAnimations(Moves.saltCure, 778);
        setMoveAuxiliaryAnimation(Moves.saltCure, "Effect", auxiliaryAnimationScriptIndices);

        // #866 Mortal Spin
        setMoveAnimations(Moves.mortalSpin);

        // #869
        setMoveAnimations(Moves.kowtowCleave);

        // #870 Flower Trick
        setMoveAnimations(Moves.flowerTrick, 768);

        // #871 Torch Song
        setMoveAnimations(Moves.torchSong);

        // #872 Aqua Step
        setMoveAnimations(Moves.aquaStep, 804);

        // + #874 Make It Rain
        cloneMoveEventHandlers(Moves.makeItRain, Moves.payDay);
        setMoveAnimations(Moves.makeItRain, 776);

        // #884 Pounce
        setMoveAnimations(Moves.pounce, 753);

        // #885 Trailblaze
        setMoveAnimations(Moves.trailblaze, 765);

        // TODO: + #889 Rage Fist
        setMoveAnimations(Moves.rageFist);

        // #890 Armor Cannon
        setMoveAnimations(Moves.armorCannon);

        // #891 Bitter Blade
        setMoveAnimations(Moves.bitterBlade);

        // #895 Aqua Cutter
        setMoveAnimations(Moves.aquaCutter, 756);

        // + #905 Electro Shot
        switch (hackMode.name) {
            case "ParagonLite" -> setMoveEventHandlers(Moves.electroShot, new MoveEventHandler(Gen5BattleEventType.onCheckChargeUpSkip, "electro_shot_charge_up_skip.s"));
            case "Redux" -> setMoveEventHandlers(Moves.electroShot,
                    new MoveEventHandler(Gen5BattleEventType.onCheckChargeUpSkip, "electro_shot_charge_up_skip.s"),
                    new MoveEventHandler(Gen5BattleEventType.onChargeUpStart, "electro_shot_charge_up_start.s"));
            default -> throw new IllegalStateException("Unexpected value: " + hackMode);
        }
        setMoveEventHandlers(Moves.electroShot,
                new MoveEventHandler(Gen5BattleEventType.onCheckChargeUpSkip, "electro_shot_charge_up_skip.s"),
                new MoveEventHandler(Gen5BattleEventType.onChargeUpStart, "electro_shot_charge_up_start.s"));
        setMoveAnimations(Moves.electroShot, 750);

        // #909 Thunderclap
        cloneMoveEventHandlers(Moves.thunderclap, Moves.suckerPunch);
        setMoveAnimations(Moves.thunderclap, 775);

        // + #912 Hard Press
        cloneMoveEventHandlers(Moves.hardPress, Moves.wringOut);
        setMoveAnimations(Moves.hardPress, 764);

        // #914 Alluring Voice
        setMoveAnimations(Moves.alluringVoice, 777);

        // TODO #915 Temper Flare
        setMoveAnimations(Moves.temperFlare);

        // #916 Supercell Slam
        cloneMoveEventHandlers(Moves.supercellSlam, Moves.jumpKick);
        setMoveAnimations(Moves.supercellSlam);

        // #917 Psychic Noise
        setMoveAnimations(Moves.psychicNoise);


        /////////////////
        /// OLD MOVES ///
        /////////////////

        // #011 Vise Grip
        setMoveAuxiliaryAnimation(Moves.viseGrip, "Trap", auxiliaryAnimationScriptIndices);

        // #013 Razor Wind
        if (hackMode.name.equals("Redux"))
            setMoveAnimations(Moves.razorWind);

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
        if (hackMode.name.equals("ParagonLite"))
            setMoveEventHandlers(Moves.tripleKick, new MoveEventHandler(Gen5BattleEventType.onGetHitCount));

        // + #190 Octazooka
        if (hackMode.name.equals("Redux"))
            setMoveEventHandlers(Moves.octazooka, new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "super_effective_vs_steel.s"));
        setMoveAnimations(Moves.octazooka, 360);

        // #200 Outrage
        if (hackMode.name.equals("ParagonLite"))
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
        if (hackMode.name.equals("ParagonLite"))
            cloneMoveEventHandlers(Moves.mirrorCoat, Moves.eruption);

        // #280 Brick Break
        setMoveEventHandlers(Moves.brickBreak,
                new MoveEventHandler(Gen5BattleEventType.onMoveDamageProcessing1),
                new MoveEventHandler(Gen5BattleEventType.onMoveDamageProcessingEnd),
                new MoveEventHandler(Gen5BattleEventType.onGetMoveDamage, "common_screen_break.s"));

        // #282 Knock Off
//        setMoveEventHandlers(Moves.knockOff,
//                new MoveEventHandler(Gen5BattleEventType.onDamageProcessingEnd_HitReal),
//                new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "knock_off.s"));

        // + #310 Astonish
        if (hackMode.name.equals("ParagonLite"))
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
        setMoveAnimations(Moves.howl);

        // #360 Gyro Ball
        if (hackMode.name.equals("ParagonLite"))
            setMoveEventHandlers(Moves.gyroBall, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "gyro_ball.s"));

        // #362 Brine
        if (hackMode.name.equals("ParagonLite"))
            setMoveEventHandlers(Moves.brine, new MoveEventHandler(Gen5BattleEventType.onGetEffectiveness, "super_effective_vs_steel.s"));

        // #368 Metal Burst
        if (hackMode.name.equals("ParagonLite"))
            cloneMoveEventHandlers(Moves.metalBurst, Moves.eruption);

        // #381 Lucky Chant
        if (hackMode.name.equals("ParagonLite"))
            cloneMoveEventHandlers(Moves.luckyChant, Moves.focusEnergy);

        // + #443 Magnet Bomb
        cloneMoveEventHandlers(Moves.magnetBomb, Moves.psystrike);

        // #449 Judgment (needs update for Pixie plate)
        setMoveEventHandlers(Moves.judgment, new MoveEventHandler(Gen5BattleEventType.onGetMoveParam, "judgment.s"));

        // + #458 Double Hit
        setMoveEventHandlers(Moves.doubleHit, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        // #486 Electro Ball
        if (hackMode.name.equals("ParagonLite"))
            setMoveEventHandlers(Moves.electroBall, new MoveEventHandler(Gen5BattleEventType.onGetMoveBasePower, "electro_ball.s"));

        // + #530 Dual Chop
        setMoveEventHandlers(Moves.dualChop, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        // #542 Hurricane
        cloneMoveEventHandlers(Moves.hurricane, Moves.thunder);

        // + #544 Gear Grind
        setMoveEventHandlers(Moves.gearGrind, new MoveEventHandler(Gen5BattleEventType.onGetHitCount, Moves.tripleKick));

        int listAddress = getMoveListAddress();
        int listCount = getMoveListCount();
        for (int i = 0; i < listCount; ++i) {
            if (battleOvl.readWord(listAddress + i * 8) == 0) {
                int dataAddress = globalAddressMap.getRomAddress(battleOvl, "Data_MoveEffectListCount");
                battleOvl.writeWord(dataAddress, i, false);
                break;
            }
        }

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

            if (spaFileNumber >= originalMoveAnimationsNarcCount) {
                byte[] existingSpaFile = moveAnimationsNarc.files.get(spaFileNumber);
                for (byte b : existingSpaFile) {
                    if (b != 0) // Should be all blank
                        throw new RuntimeException(String.format("Attempted to overwrite existing SPA file #%03d for move #%03d %s",
                                spaFileNumber, moveNumber, moves.get(moveNumber).name));
                }
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
            byte[] spaFileData = readBytes(String.format("moveanims/spa/%03d_%s_%s_%03d.spa", moveNumber, formattedMoveName, mode.toLowerCase(), spaFileNumber));
            while (moveAnimationsNarc.files.size() <= spaFileNumber) {
                moveAnimationsNarc.files.add(new byte[16]);
            }

            byte[] existingSpaFile = moveAnimationsNarc.files.get(spaFileNumber);
            for (byte b : existingSpaFile) {
                if (b != 0) // Should be all blank
                    throw new RuntimeException(String.format("Attempted to overwrite existing SPA file #%03d for move #%03d %s",
                            spaFileNumber, moveNumber, moveNames.get(moveNumber)));
            }

            moveAnimationsNarc.files.set(spaFileNumber, spaFileData);
        }
        byte[] script = readBytes(String.format("moveanims/scripts/%03d_%s_%s.bin", moveNumber, formattedMoveName, mode.toLowerCase()));

        String auxiliaryAnimationName = String.format("%s_%s", formattedMoveName, mode.toLowerCase());
        if (!auxiliaryAnimationScriptIndices.containsKey(auxiliaryAnimationName))
            throw new RuntimeException(String.format("Could not find auxiliary animation with id \"%s\"", auxiliaryAnimationName));
        int auxiliaryAnimationScriptIndex = auxiliaryAnimationScriptIndices.get(auxiliaryAnimationName);

        while (battleAnimationScriptsNarc.files.size() <= auxiliaryAnimationScriptIndex)
            battleAnimationScriptsNarc.files.add(getDefaultAnimationScript());
        battleAnimationScriptsNarc.files.set(auxiliaryAnimationScriptIndex, script);

        formattedMoveName = moves.get(moveNumber).name.replaceAll("[ -]", "");
        armParser.addGlobalValue(String.format("BTLANM_%s_%s", formattedMoveName, mode), auxiliaryAnimationScriptIndex);
    }

    private void setBattleAnimation(int globalScriptNumber, String filename, int... SpaFiles) {
        // TODO: Make this a global param somewhere
        int battleAnimationScriptsOffset = 561;

        for (int spaFileNumber : SpaFiles) {
            byte[] spaFileData = readBytes(String.format("moveanims/spa/%s_%03d.spa", filename, spaFileNumber));
            while (moveAnimationsNarc.files.size() <= spaFileNumber) {
                moveAnimationsNarc.files.add(new byte[16]);
            }

            byte[] existingSpaFile = moveAnimationsNarc.files.get(spaFileNumber);
            for (byte b : existingSpaFile) {
                if (b != 0) // Should be all blank
                    throw new RuntimeException(String.format("Attempted to overwrite existing SPA file #%03d for %s", spaFileNumber, filename));
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
        loadReduxMoveTexts();

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
//                if (!moveNameCheck.equalsIgnoreCase(nameStrCheck) && num <= Moves.fusionBolt) {
//                    throw new RuntimeException(String.format("Move names didn't match: %s and %s", move.name, nameStr));
//                }

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

    private void loadReduxMoveTexts() {
        Scanner sc;
        try {
            sc = new Scanner(FileFunctions.openConfig("paragonlite/redux_movetexts.tsv"), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (int moveNumber = 0; sc.hasNextLine(); ++moveNumber) {
            String[] line = sc.nextLine().split("\t");
            if (line.length == 0)
                continue;

            String name = line[0];
            String description = line.length >= 2 ? line[1] : "";

            if (moveNumber < moveNames.size())
                moveNames.set(moveNumber, name);
            if (moveNumber < moveDescriptions.size())
                moveDescriptions.set(moveNumber, description);

            if (moveNumber < moves.size()) {
                Move move = moves.get(moveNumber);
                move.name = name;
                move.description = description;
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

    public void setTMsAndHMs(Settings settings) {
        String filename = hackMode.tmsAndHMsFile;
        if (filename.isEmpty()) {
            return;
        }

        List<String> lines = readLines(filename);
        List<String> descriptions = new ArrayList<>(lines.size());
        Map<String, Integer> moveNameToItem = new HashMap<>(lines.size());
        for (String line : lines) {
            if (line.isEmpty())
                continue;

            String[] components = line.split("\t");
            String name = components[0];
            String description = components[1].replace("\\n", "\n");

            moveNameToItem.put(name, moveNameToItem.size());
            descriptions.add(description);
        }

        int total = moveNameToItem.size();

        List<Integer> tmAndHMMoves = new ArrayList<>(total);
        for (int i = 0; i < total; ++i)
            tmAndHMMoves.add(0);

        int count = 0;
        for (int i = 0; i < moves.size() && count < total; ++i) {
            Move move = moves.get(i);
            String moveName = move.name;
            if (!moveNameToItem.containsKey(moveName))
                continue;

            int index = moveNameToItem.get(moveName);
            move.shopDescription = descriptions.get(index);
            tmAndHMMoves.set(index, i);
            ++count;
        }

        romHandler.setTMMoves(settings, tmAndHMMoves);
    }

    public void setItems() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        registerItemEffects();

        updateNaturalGiftPowers();

        relocateItemListRamAddress(11);

        // #219 Mental Herb
        setMentalHerb();

        // #230 Focus Band
        setFocusBand();

        // #235 Dragon Scale
        setDragonScale();

        // #269 Light Clay
        setLightClay();

        // #271 Power Herb
        setPowerHerb();

        // #275 Focus Sash
        setFocusSash();

        // Weather change item
        if (hackMode.name.equals("ParagonLite")) {
            List<String> commonWeatherChangeItemCheckLines = readLines("eventhandlers/item/common_weather_change_item_check.s");
            battleOvl.writeCode(commonWeatherChangeItemCheckLines, "CommonWeatherChangeItemCheck", true);

            List<String> commonWeatherChangeItemUseLines = readLines("eventhandlers/item/common_weather_change_item_use.s");
            battleOvl.writeCode(commonWeatherChangeItemUseLines, "CommonWeatherChangeItemUse", true);

            // #282 Icy Rock
            setIcyRock();

            // #283 Smooth Rock
            setSmoothRock();

            // #284 Heat Rock
            setHeatRock();

            // #285 Damp Rock
            setDampRock();
        }

        // #298 Flame Plate - #313 Iron Plate
        if (hackMode.name.equals("ParagonLite")) {
            for (int i = Items.flamePlate; i <= Items.ironPlate; ++i) {
                setTypeChangePlate(i);
            }
        }

        // #321 Protector
        if (hackMode.name.equals("ParagonLite"))
            setProtector();

        // #541 Air Balloon
        setAirBalloon();

        // #542 Red Card
        setRedCard();

        // #545 Absorb Bulb
        setAbsorbBulb();

        // #546 Cell Battery
        setCellBattery();

        // #547 Eject Button
        setEjectButton();

        // #548 Fire Gem - #564 Normal Gem
        if (!hackMode.name.equals("Redux")) {
            for (int i = Items.fireGem; i <= Items.normalGem; ++i) {
                setItemIsConsumable(i, false);
            }
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

        // #217 Quick Claw
        addQuickClaw();

        // #518 Blank Plate
        addBlankPlate();

        // #521 Clear Amulet
        addClearAmulet();

        // #524 Covert Cloak
        addCovertCloak();

        // #525 Loaded Dice
        addLoadedDice();

        // #527 Fairy Feather
        addFairyFeather();

        int listAddress = getItemListAddress();
        int listCount = getItemListCount();
        for (int i = 0; i < listCount; ++i) {
            if (battleOvl.readWord(listAddress + i * 8) == 0) {
                int dataAddress = globalAddressMap.getRomAddress(battleOvl, "Inst_ItemEffectListCountCmp");
                battleOvl.writeByte(dataAddress, i);
                break;
            }
        }

        // Berries
        setBerryList();

        System.out.println("Set items");
    }

    // In Gen VI, all Berries got a +20 boost to power for Natural Gift across the board.
    // We've decided to make this +30 (or +10 to Gen VI+ power) as it's a one-time use move.
    // This means berries like Cheri or Oran now do 90 (originally 60 in Gen V and 80 in Gen VI+)
    // and berries like Liechi or Rowap now do 110 (originally 80 and 100)
    void updateNaturalGiftPowers() {
        int increase = 20;

        if (hackMode.name.equals("ParagonLite"))
            increase = 30;

        for (int i = 0; i < itemDataNarc.files.size(); ++i) {
            int power = getItemNaturalGiftPower(i);
            if (power > 0)
                setItemNaturalGiftPower(i, power + increase);
        }
    }

    void setMentalHerb() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.mentalHerb, false);
    }

    void setFocusBand() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.focusBand, false);
    }

    void setDragonScale() {
        if (!hackMode.name.equals("ParagonLite"))
            return;

        int number = Items.dragonScale;

        itemDescriptions.set(number, "An item to be held by a Pokémon. This\\xFFFEmystical scale reduces damage taken\\xFFFEfrom all incoming attacks.");

        setItemEffect(number, 147); // TODO

        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "dragon_scale.s"));
    }

    void setLightClay() {
        int number = Items.lightClay;

        // Updated to work with Aurora Veil
        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onCheckSideConditionParam, "light_clay.s"));
    }

    void setPowerHerb() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.powerHerb, false);
    }

    void setFocusSash() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.focusSash, false);
    }

    void setIcyRock() {
        int index = Items.icyRock;

        itemDescriptions.set(index, "An item to be held by a Pokémon.\\xFFFEThis rock summons a hailstorm\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(index,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "icy_rock_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onRotateIn, "icy_rock_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItem, "icy_rock_use.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItemTemp, "icy_rock_use.s"),
                new ItemEventHandler(Gen5BattleEventType.onCheckItemReaction, "icy_rock_immune.s"));
    }

    void setSmoothRock() {
        int number = Items.smoothRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock summons a sandstorm\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "smooth_rock_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onRotateIn, "smooth_rock_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItem, "smooth_rock_use.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItemTemp, "smooth_rock_use.s"),
                new ItemEventHandler(Gen5BattleEventType.onCheckItemReaction, "smooth_rock_immune.s"));
    }

    void setHeatRock() {
        int number = Items.heatRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock turns the sunlight harsh\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "heat_rock_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onRotateIn, "heat_rock_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItem, "heat_rock_use.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItemTemp, "heat_rock_use.s"));
    }

    void setDampRock() {
        int number = Items.dampRock;

        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis rock makes it rain\\xFFFEwhen the holder enters battle.");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "damp_rock_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onRotateIn, "damp_rock_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItemTemp, "damp_rock_use.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItem, "damp_rock_use.s"));
    }

    void setTypeChangePlate(int number) {
        Type type = Gen5Constants.plateToType.get(number);
        itemDescriptions.set(number, String.format("An item to be held by a Pokémon.\\xFFFEThis mysterious tablet changes\\xFFFEthe holder's type to %s.", type.camelCase()));

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onSwitchIn, "common_plate_item_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onRotateIn, "common_plate_item_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItem, "common_plate_item_use.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItemTemp, "common_plate_item_use.s"));
    }

    void setProtector() {
        int number = Items.protector;

        itemDescriptions.set(number, "An item to be held by a Pokémon. This\\xFFFEarmor boosts the holder's Defense stat\\xFFFEbut prevents the use of status moves.");

        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "protector.s"));
    }

    void setAirBalloon() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.airBalloon, false);
    }

    void setRedCard() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.redCard, false);
    }

    void setAbsorbBulb() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.absorbBulb, false);
    }

    void setCellBattery() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.cellBattery, false);
    }

    void setEjectButton() {
        if (!hackMode.name.equals("Redux"))
            setItemIsConsumable(Items.ejectButton, false);
    }

    void setBigNugget() {
        setItemFlingPower(Items.bigNugget, 130);
    }

    void addWeaknessPolicy() {
        int number = ParagonLiteItems.weaknessPolicy;

        setItemName(number, "Weakness Policy", "Weakness Policies");
        itemDescriptions.set(number, "An item to be held by a Pokémon. Attack\\xFFFEand Sp. Atk sharply increase if the\\xFFFEholder is hit with a move it's weak to.");

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = 100;
        itemData.effect = 147; // TODO
        itemData.flingPower = 80;
        itemData.type = Item.ItemType.HELD;
        itemData.isConsumable = hackMode.name.equals("Redux");
        addAllItemData(number, itemData);

        setItemSprite(number, "weakness_policy");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onMoveDamageReaction1, "weakness_policy_on_hit.s"),
                new ItemEventHandler(Gen5BattleEventType.onUseItem, "weakness_policy_boost.s"));
    }

    void addAssaultVest() {
        int number = ParagonLiteItems.assaultVest;

        setItemName(number, "Assault Vest", "Assault Vests");
        itemDescriptions.set(number, "An item to be held by a Pokémon. This\\xFFFEvest boosts the holder's Sp. Def stat\\xFFFEbut prevents the use of status moves.");

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = 100;
        itemData.effect = 147; // TODO
        itemData.flingPower = 80;
        itemData.type = Item.ItemType.HELD;
        addAllItemData(number, itemData);

        setItemSprite(number, "assault_vest");

        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onGetDefendingStatValue, "assault_vest.s"));
    }

    void addPixiePlate() {
        int number = ParagonLiteItems.pixiePlate;

        setItemName(number, "Pixie Plate", "Pixie Plates");
        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEIt is a stone tablet that boosts the\\xFFFEpower of Fairy-type moves.");

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = getItemPrice(Items.flamePlate);
        itemData.effect = 147; // TODO
        itemData.effectParam = 20; // 1.2x
        itemData.flingPower = getItemFlingPower(Items.flamePlate);
        itemData.type = Item.ItemType.HELD;
        addAllItemData(number, itemData);

        setItemSprite(number, Items.flamePlate, "pixie_plate");

        if (hackMode.name.equals("ParagonLite")) {
            setTypeChangePlate(number);
        } else {
            setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "pixie_plate.s"));
        }
    }

    void addRoseliBerry() {
        int number = ParagonLiteItems.roseliBerry;

        setItemName(number, "Roseli Berry", "Roseli Berries");
        itemDescriptions.set(number, "Weakens a supereffective Fairy-type\\xFFFEattack against the holding Pokémon.");

        ItemData itemData = new ItemData(Item.FieldPocket.BERRIES);
        itemData.price = getItemPrice(Items.occaBerry);
        itemData.effect = 147; // TODO
        itemData.flingPower = getItemFlingPower(Items.occaBerry);
        itemData.naturalGiftPower = getItemNaturalGiftPower(Items.occaBerry);
        itemData.naturalGiftType = Type.FAIRY;
        itemData.isConsumable = getItemIsConsumable(Items.occaBerry);
        addAllItemData(number, itemData);

        setItemSprite(number, "roseli_berry");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onMoveDamageProcessing2, "roseli_berry_super_effective_check.s"),
                new ItemEventHandler(Gen5BattleEventType.onPostDamageReaction, Items.occaBerry));
    }

    void addFairyGem() {
        int number = ParagonLiteItems.fairyGem;

        setItemName(number, "Fairy Gem", "Fairy Gems");
        String description = itemDescriptions.get(Items.fireGem).replace("Fire", "Fairy");
        itemDescriptions.set(number, description);

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = getItemPrice(Items.fireGem);
        itemData.effect = 147; // TODO
        itemData.type = Item.ItemType.HELD;
        itemData.isConsumable = getItemIsConsumable(Items.fireGem);
        addAllItemData(number, itemData);

        setItemSprite(number, Items.fireGem, "fairy_gem");

        setItemEventHandlers(number,
                new ItemEventHandler(Gen5BattleEventType.onDamageProcessingStart, "fairy_gem_work.s"),
                new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "fairy_gem_damage_boost.s"),
                new ItemEventHandler(Gen5BattleEventType.onDamageProcessingEnd, Items.fireGem));
    }

    void addQuickClaw() {
        int number = Items.quickClaw;

        switch (hackMode.name) {
            case "ParagonLite" -> {
                // Description
                itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEA light, sharp claw that slightly\\xFFFEboosts the user's speed.");

                // Data
                setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onCalcSpeed, "quick_claw.s"));
            }
            case "Redux" -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + hackMode);
        }
    }

    void addBlankPlate() {
        int number = ParagonLiteItems.blankPlate;

        setItemName(number, "Blank Plate", "Blank Plates");
        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEIt is a stone tablet that boosts the\\xFFFEpower of Normal-type moves.");

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = getItemPrice(Items.flamePlate);
        itemData.effect = 147; // TODO
        itemData.effectParam = 20; // 1.2x
        itemData.flingPower = getItemFlingPower(Items.flamePlate);
        itemData.type = Item.ItemType.HELD;
        addAllItemData(number, itemData);

        setItemSprite(number, Items.flamePlate, "blank_plate");

        if (hackMode.name.equals("ParagonLite")) {
            setTypeChangePlate(number);
        } else {
            setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "blank_plate.s"));
        }
    }

    void addClearAmulet() {
        int number = ParagonLiteItems.clearAmulet;

        setItemName(number, "Clear Amulet", "Clear Amulets");
        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEThis amulet prevents other Pokémon\\xFFFEfrom lowering the holder's stats.");

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = 100;
        itemData.effect = 147; // TODO
        itemData.flingPower = 30;
        itemData.type = Item.ItemType.HELD;
        addAllItemData(number, itemData);

        setItemSprite(number, "clear_amulet");

        setItemEventHandlers(number, new ItemEventHandler(0x5B, "clear_amulet_5B"), new ItemEventHandler(0x5C, "clear_amulet_5C"));
    }

    void addCovertCloak() {
        int number = ParagonLiteItems.covertCloak;

        setItemName(number, "Covert Cloak", "Covert Cloaks");
        itemDescriptions.set(number, "An item to be held by a Pokémon. This\\xFFFEcloak conceals the holder, protecting\\xFFFEit from the additional effects of moves.");

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = 100;
        itemData.effect = 147; // TODO
        itemData.flingPower = 30;
        itemData.type = Item.ItemType.HELD;
        addAllItemData(number, itemData);

        setItemSprite(number, "covert_cloak");

        cloneItemEventHandlersFromAbility(number, Abilities.shieldDust);
    }

    void addLoadedDice() {
        int number = ParagonLiteItems.loadedDice;

        setItemName(number, "Loaded Dice", "Loaded Dice");
        itemDescriptions.set(number, "An item to be held by a Pokémon.\\xFFFEIt always rolls a good number, ensuring\\xFFFEthat multistrike moves hit more times.");

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = 100;
        itemData.effect = 147; // TODO
        itemData.flingPower = 30;
        itemData.type = Item.ItemType.HELD;
        addAllItemData(number, itemData);

        setItemSprite(number, "loaded_dice");

        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onGetHitCount, "loaded_dice"));
    }

    void addFairyFeather() {
        int number = ParagonLiteItems.fairyFeather;

        setItemName(number, "Fairy Feather", "Fairy Feathers");
        itemDescriptions.set(number, "An item to be held by a Pokémon. This\\xFFFEfeather, which gleams faintly, boosts\\xFFFEthe power of the holder's Fairy-type moves.");

        ItemData itemData = new ItemData(Item.FieldPocket.ITEMS);
        itemData.price = getItemPrice(Items.charcoal);
        itemData.effect = 147; // TODO
        itemData.effectParam = 20; // 1.2x
        itemData.flingPower = 10;
        itemData.type = Item.ItemType.HELD;
        addAllItemData(number, itemData);

        setItemSprite(number, "fairy_feather");

        setItemEventHandlers(number, new ItemEventHandler(Gen5BattleEventType.onGetMovePower, "pixie_plate.s"));
    }

    private static class ItemData {
        int price = 0;
        int effect = 0;
        int effectParam = 0;
        Item.TempUseEffect pluckEffect = Item.TempUseEffect.NONE;
        Item.TempUseEffect flingEffect = Item.TempUseEffect.NONE;
        int flingPower = 0;
        int naturalGiftPower = 0;
        Type naturalGiftType = null;
        boolean isImportant = false;
        boolean canRegister = false;
        Item.FieldPocket fieldPocket = null;
        Set<Item.BattlePocket> battlePockets = null;
        Item.FieldFuncType fieldFunction = Item.FieldFuncType.NONE;
        Item.BattleFuncType battleFunction = Item.BattleFuncType.NONE;
        int workType = 0;
        Item.ItemType type = Item.ItemType.NONE;
        boolean isConsumable = false;

        ItemData(Item.FieldPocket fieldPocket) {
            this.fieldPocket = fieldPocket;
        }
    }

    void addAllItemData(int number, ItemData data) {
        if (data.fieldPocket == Item.FieldPocket.ITEMS && data.type == Item.ItemType.NONE)
            throw new RuntimeException("item type must not be NONE if in the Items field pocket");

        setItemPrice(number, data.price);
        setItemEffect(number, data.effect);
        setItemEffectParam(number, data.effectParam);
        setItemPluckEffect(number, data.pluckEffect);
        setItemFlingEffect(number, data.flingEffect);
        setItemFlingPower(number, data.flingPower);
        setItemNaturalGiftPower(number, data.naturalGiftPower);
        setItemNaturalGiftType(number, data.naturalGiftType);
        setItemIsImportant(number, data.isImportant);
        setItemCanRegister(number, data.canRegister);
        setItemFieldPocket(number, data.fieldPocket);
        if (data.battlePockets != null && !data.battlePockets.isEmpty())
            setItemBattlePockets(number, data.battlePockets.toArray(Item.BattlePocket[]::new));
        setItemFieldFunction(number, data.fieldFunction);
        setItemBattleFunction(number, data.battleFunction);
        setItemWorkType(number, data.workType);
        setItemType(number, data.type);
        setItemIsConsumable(number, data.isConsumable);
        primeItemSortIndex(number);
    }

    private void setItemName(int itemNumber, String name, String pluralName) {
        itemNames.set(itemNumber, name);
        itemNameMessages.set(itemNumber, "\uF000봁\\x0000a \uF000\uFF00\\x0001ÿ" + name);
        itemPluralNames.set(itemNumber, pluralName);
    }

    // 0x00
    private void setItemPrice(int itemNumber, int price) {
        if (price % 10 != 0)
            throw new RuntimeException("Price must be a multiple of 10");

        byte[] data = itemDataNarc.files.get(itemNumber);
        writeHalf(data, 0x00, price / 10);
    }

    private int getItemPrice(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        return readHalf(data, 0x00) * 10;
    }

    // 0x02
    private void setItemEffect(int itemNumber, int effectId) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x02] = (byte) effectId;
    }

    // 0x03
    private void setItemEffectParam(int itemNumber, int value) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x03] = (byte) value;
    }

    // 0x04
    private void setItemPluckEffect(int itemNumber, Item.TempUseEffect effect) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x04] = (byte) effect.ordinal();
    }

    private Item.TempUseEffect getItemPluckEffect(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        return Item.TempUseEffect.values()[data[0x04] & 0xFF];
    }

    // 0x05
    private void setItemFlingEffect(int itemNumber, Item.TempUseEffect effect) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x05] = (byte) effect.ordinal();
    }

    private Item.TempUseEffect getItemFlingEffect(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        return Item.TempUseEffect.values()[data[0x05] & 0xFF];
    }

    // 0x06
    private void setItemFlingPower(int itemNumber, int power) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x06] = (byte) power;
    }

    private int getItemFlingPower(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        return data[0x06] & 0xFF;
    }

    // 0x07
    private void setItemNaturalGiftPower(int itemNumber, int power) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x07] = (byte) power;
    }

    private int getItemNaturalGiftPower(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        return data[0x07] & 0xFF;
    }

    // 0x08 [0-4]
    private void setItemNaturalGiftType(int itemNumber, Type type) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        int typeBits = type != null ? Gen5Constants.typeToByte(type) : -1;
        writeWordBits(data, 0x08, 0x00, 5, typeBits);
    }

    // 0x08 [5]
    private void setItemIsImportant(int itemNumber, boolean isImportant) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        writeWordBit(data, 0x08, 0x05, isImportant);
    }

    // 0x08 [6]
    private void setItemCanRegister(int itemNumber, boolean canRegister) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        writeWordBit(data, 0x08, 0x06, canRegister);
    }

    // 0x08 [7-A]
    private void setItemFieldPocket(int itemNumber, Item.FieldPocket fieldPocket) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        writeWordBits(data, 0x08, 0x07, 4, fieldPocket.ordinal());

        if (romHandler.isUpperVersion()) {
            ParagonLiteAddressMap.AddressBase addressBase = globalAddressMap.getAddressData(arm9, "Data_ItemPockets");
            arm9.writeByte(addressBase.address + itemNumber, (byte) fieldPocket.ordinal());
        }
    }

    private Item.FieldPocket getItemFieldPocket(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        int pocketId = readWordBits(data, 0x08, 0x07, 4);
        if (pocketId < 0 || pocketId >= Item.FieldPocket.values().length)
            throw new RuntimeException();

        return Item.FieldPocket.values()[pocketId];
    }

    // 0x08 [B-F]
    private void setItemBattlePockets(int itemNumber, Item.BattlePocket... battlePockets) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        int combined = 0;
        for (Item.BattlePocket battlePocket : battlePockets) {
            combined |= 1 << battlePocket.ordinal();
        }

        writeWordBits(data, 0x08, 0x07, 4, combined);
    }

    // 0x0A Field Function
    private void setItemFieldFunction(int itemNumber, Item.FieldFuncType fieldFunction) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        data[0x0A] = fieldFunction.getByteValue();
    }

    // 0x0B Battle Function
    private void setItemBattleFunction(int itemNumber, Item.BattleFuncType battleFunction) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        data[0x0B] = (byte) battleFunction.ordinal();
    }

    // 0x0C
    private void setItemWorkType(int itemNumber, int workType) {
        byte[] data = itemDataNarc.files.get(itemNumber);

        data[0x0C] = (byte) workType;
    }

    // 0x0D
    private void setItemType(int itemNumber, Item.ItemType itemType) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x0D] = (byte) itemType.ordinal();
    }

    private Item.ItemType getItemType(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        return Item.ItemType.values()[data[0x0D] & 0xFF];
    }

    // 0x0E
    private void setItemIsConsumable(int itemNumber, boolean isConsumable) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x0E] = (byte) (isConsumable ? 1 : 0);
    }

    private boolean getItemIsConsumable(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        return data[0x0E] != 0;
    }

    // 0x0F
    // Assigns sort index to 0x00; 0xFF is used internally for unsorted items that may be replaced by the new ones here.
    private void primeItemSortIndex(int itemNumber) {
        byte[] data = itemDataNarc.files.get(itemNumber);
        data[0x0F] = 0x00;
    }

    private void setBerryList() {
        int funcRomAddress = globalAddressMap.getRomAddress(arm9, "IsItemBerry");
        int listCountAddress = funcRomAddress + 0x12;
        int listRefAddress = funcRomAddress + 0x1C;
        int numBerriesOld = 64;
        int[] newBerries = new int[]{
                ParagonLiteItems.roseliBerry,
                ParagonLiteItems.keeBerry,
                ParagonLiteItems.marangaBerry
        };

        int numBerries = numBerriesOld + newBerries.length;

        int listRomAddress = arm9.ramToRomAddress(arm9.readWord(listRefAddress));
        int[] newList = new int[numBerries];
        for (int i = 0; i < numBerriesOld; ++i) {
            int berryNumber = arm9.readUnsignedHalfword(listRomAddress + i * 2);
            newList[i] = berryNumber;
        }
        System.arraycopy(newBerries, 0, newList, numBerriesOld, newBerries.length);

        byte[] newBytes = new byte[numBerries * 2];
        for (int i = 0; i < numBerries; ++i) {
            writeHalf(newBytes, i * 2, newList[i]);
        }

        arm9.writeByte(listCountAddress, numBerries);

        ParagonLiteAddressMap.DataAddress dataAddress = arm9.replaceData(newBytes, "Data_BerryIDs");
        int newRamAddress = dataAddress.getRamAddress();
        arm9.writeWord(listRefAddress, newRamAddress, true);
    }

    public void test() {
        System.out.println("- test");

//        battleOvl.writeHalfword(0x021A9BB4, 0x2801);

//        disableRandomness();

        List<Trainer> trainers = romHandler.getTrainers();
//        romHandler.setTrainers(trainers, true, true);

//        for (Trainer tr : trainers) {
//            if (tr.pokemon.isEmpty())
//                continue;
//
//            tr.setPokemonHaveCustomMoves(true);
//            tr.setPokemonHaveItems(true);
//
//            // Poke 1
//            {
//                TrainerPokemon poke1 = tr.pokemon.get(0);
//                poke1.pokemon = romHandler.getPokemon().get(Species.cofagrigus);
//                pokes[poke1.pokemon.number].ability1 = Abilities.goodAsGold;
//                poke1.abilitySlot = 1;
//                poke1.level = 53;
//                poke1.moves = new int[]{Moves.haze, Moves.meanLook, Moves.hex, 0};
//                poke1.heldItem = Items.sitrusBerry;
//                poke1.IVs = 0;
//            }
//
////            // Poke 2
////            {
////                if (tr.pokemon.size() < 2)
////                    tr.pokemon.add(tr.pokemon.get(0).copy());
////                TrainerPokemon poke2 = tr.pokemon.get(1);
////                poke2.pokemon = romHandler.getPokemon().get(Species.cresselia);
////                pokes[poke2.pokemon.number].ability1 = Abilities.illuminate;
////                poke2.abilitySlot = 1;
////                poke2.level = 16;
////                poke2.moves = new int[]{Moves.gust, Moves.flowerTrick, Moves.silverWind, Moves.mistBall};
////                poke2.IVs = 0;
////                poke2.heldItem = Items.sitrusBerry;
////            }
//
////            if (tr.pokemon.size() < 3)
////                tr.pokemon.add(poke1.copy());
//        }
//
//        romHandler.setTrainers(trainers, false, true);

        // Set debug AI Flag
//        for (Trainer tr : trainers) {
//            tr.aiFlags = (1 << 14);
//        }
    }

    private void researchGetEffectVals(int listCount, int listAddress) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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

        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        // TODO: Reimplement this somehow
        int battleRandAddress = battleOvl.find("64 21 08 1A 00 04 00 0C 78 43 64 21");
        battleOvl.writeHalfword(battleRandAddress + 2, 0x2064);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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

    private SortedSet<Integer> getExistingAbilities() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int listAddress = getAbilityListAddress();
        int count = getAbilityListCount();

        SortedSet<Integer> abilities = new TreeSet<>();

        for (int i = 0; i < count; ++i) {
            int ability = battleOvl.readWord(listAddress + 8 * i);
            if (ability > 0)
                abilities.add(ability);
        }

        return abilities;
    }

    private void relocateAbilityListRamAddress(int additionalCount) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        return globalAddressMap.getRamAddress(battleOvl, "Data_AbilityEffectList");
    }

    private int getMoveListAddress() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        return globalAddressMap.getRamAddress(battleOvl, "Data_MoveEffectList");
    }

    private int getItemListAddress() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        return globalAddressMap.getRamAddress(battleOvl, "Data_ItemEffectList");
    }

    private int getRedirectorCountSetAddress(int funcAddress) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int countSetAddress = getRedirectorCountSetAddress(redirectorAddress);
        if (countSetAddress <= 0) throw new RuntimeException();

        return battleOvl.readUnsignedByte(countSetAddress);
    }

    private int getEventHandlerListAddressFromRedirector(int redirectorAddress) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int eventListReferenceAddress = getRedirectorListReferenceAddress(redirectorAddress);
        if (eventListReferenceAddress <= 0) throw new RuntimeException();

        return battleOvl.readWord(eventListReferenceAddress);
    }

    private int getRedirectorAddress(int objectNumber, int objectListAddress, int objectListCount) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
            ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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

                battleOvl.writeCode(lines, fullFuncName, false);
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
            ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
            ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
            ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        List<String> lines = Arrays.asList(
                "mov r1, #" + eventHandlers.length,
                "str r1, [r0]",
                "ldr r0, =" + eventHandlerListAddress,
                "bx lr");
        int redirectorFuncAddress = battleOvl.writeCodeUnnamed(lines, false);

        // Write to object list
        battleOvl.writeWord(objectListAddress + index * 8, number, false);
        battleOvl.writeWord(objectListAddress + index * 8 + 4, redirectorFuncAddress + 1, true);
    }

    private void setAbilityEventHandlers(int abilityNumber, List<AbilityEventHandler> events) {
        AbilityEventHandler[] eventsArray = new AbilityEventHandler[events.size()];
        for (int i = 0; i < events.size(); ++i)
            eventsArray[i] = events.get(i);
        setAbilityEventHandlers(abilityNumber, eventsArray);
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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int itemListAddress = getItemListAddress();
        int listIndex = getBattleObjectIndex(itemListAddress, getItemListCount(), itemNumber);
        if (listIndex < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", itemNames.get(itemNumber)));

        int abilityRedirectorAddress = getAbilityRedirectorAddress(abilityNumber);
        battleOvl.writeWord(itemListAddress + listIndex * 8, itemNumber, false);
        battleOvl.writeWord(itemListAddress + listIndex * 8 + 4, abilityRedirectorAddress + 1, true);
    }

    private void clearMoveEventHandlers(int moveNumber) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

        int moveListAddress = getMoveListAddress();
        int moveListCount = getMoveListCount();
        int index = getBattleObjectIndex(moveListAddress, moveListCount, moveNumber);
        if (index < 0)
            throw new RuntimeException(String.format("Could not find event handlers for %s", moves.get(moveNumber).name));

        battleOvl.writeWord(moveListAddress + index * 8, 0, false);
        battleOvl.writeWord(moveListAddress + index * 8 + 4, 0, true);
    }

    private int getAbilityListCount() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        int cmpInstructionAddress = globalAddressMap.getRamAddress(battleOvl, "Inst_AbilityEffectListCountCmp");
        return battleOvl.readUnsignedByte(cmpInstructionAddress);
    }

    private int getMoveListCount() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        int dataAddress = globalAddressMap.getRamAddress(battleOvl, "Data_MoveEffectListCount");
        return battleOvl.readWord(dataAddress);
    }

    private int getItemListCount() {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);
        int cmpInstructionAddress = globalAddressMap.getRamAddress(battleOvl, "Inst_ItemEffectListCountCmp");
        return battleOvl.readUnsignedByte(cmpInstructionAddress);
    }

    private int getBattleObjectIndex(int listRamAddress, int listCount, int objectNumber) {
        ParagonLiteOverlay battleOvl = overlays.get(OverlayId.BATTLE);

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

    private static void writeBit(byte[] data, int byteOffset, int bitOffset, boolean value) {
        writeBits(data, byteOffset, bitOffset, 1, value ? 1 : 0);
    }

    private static void writeBits(byte[] data, int byteOffset, int bitOffset, int bitLength, int value) {
        if (bitLength + bitOffset > 8)
            throw new RuntimeException(String.format("Could not fit value of length %d at offset %d", bitLength, bitOffset));

        byte b = (byte) writeBitsHelper(data[byteOffset], bitOffset, bitLength, value);
        data[byteOffset] = b;
    }

    private static void writeHalf(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private static void writeHalfBit(byte[] data, int byteOffset, int bitOffset, boolean value) {
        writeHalfBits(data, byteOffset, bitOffset, 1, value ? 1 : 0);
    }

    private static void writeHalfBits(byte[] data, int byteOffset, int bitOffset, int bitLength, int value) {
        if (bitLength + bitOffset > 16)
            throw new RuntimeException(String.format("Could not fit value of length %d at offset %d", bitLength, bitOffset));

        int half = writeBitsHelper(readHalf(data, byteOffset), bitOffset, bitLength, value);
        writeHalf(data, byteOffset, half);
    }

    private static int readHalf(byte[] data, int offset) {
        return data[offset] | (data[offset + 1] << 8);
    }

    private static void writeWord(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xFF);
        data[offset + 1] = (byte) ((value >> 8) & 0xFF);
        data[offset + 2] = (byte) ((value >> 16) & 0xFF);
        data[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

    private static void writeWordBit(byte[] data, int byteOffset, int bitOffset, boolean value) {
        writeWordBits(data, byteOffset, bitOffset, 1, value ? 1 : 0);
    }

    private static void writeWordBits(byte[] data, int byteOffset, int bitOffset, int bitLength, int value) {
        if (bitLength + bitOffset > 32)
            throw new RuntimeException(String.format("Could not fit value of length %d at offset %d", bitLength, bitOffset));

        int word = writeBitsHelper(readWord(data, byteOffset), bitOffset, bitLength, value);
        writeHalf(data, byteOffset, word);
    }

    private static int readWord(byte[] data, int offset) {
        return data[offset] | (data[offset + 1] << 8) | (data[offset + 1] << 16) | (data[offset + 1] << 24);
    }

    private static int readWordBits(byte[] data, int byteOffset, int bitOffset, int bitLength) {
        if (bitLength + bitOffset > 32)
            throw new RuntimeException(String.format("Could not fit value of length %d at offset %d", bitLength, bitOffset));

        return readBitsHelper(readWord(data, byteOffset), bitOffset, bitLength);
    }

    private static int writeBitsHelper(int full, int bitOffset, int bitLength, int value) {
        int mask = (1 << bitLength) - 1;
        if ((Math.abs(value) & mask) != Math.abs(value))
            throw new RuntimeException(String.format("Could not fit value %d in bit length of %d", value, bitLength));

        return (full & ~(mask << bitOffset)) | ((value & mask) << bitOffset);
    }

    private static int readBitsHelper(int full, int bitOffset, int bitLength) {
        int mask = ((1 << bitLength) - 1) << bitOffset;

        return (full & mask) >> bitLength;
    }
}
