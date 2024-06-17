package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.arm.ArmParser;
import com.dabomstew.pkrandom.constants.*;
import com.dabomstew.pkrandom.newnds.NARCArchive;
import com.dabomstew.pkrandom.pokemon.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class ParagonLiteHandler {

    Gen5RomHandler romHandler;

    byte[] arm9;

    Map<String, Map<String, Integer>> addressMap;

    Map<Integer, Integer> referenceCounts;

    ParagonLiteOverlay battleOvl;
    ParagonLiteOverlay trainerAIOvl;

    Pokemon[] pokes;
    Move[] moves;

    List<String> battleEventStrings;

    List<String> abilityNames;
    List<String> abilityDescriptions;
    List<String> abilityExplanations;

    List<String> moveNames;
    List<String> moveDescriptions;

    // Update
    List<String> oldAbilityNames;
    List<String> oldMoveNames;
    Map<Integer, String> abilityUpdates;
    PokeUpdate[] pokeUpdates;

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

        boolean hasAnyUpdate() {
            return hasTypeUpdate() || hasAbilityUpdate() || hasStatsUpdate() || hasExpYieldUpdate();
        }

        boolean hasTypeUpdate() {
            return type1 != 0 || type2 != 0;
        }

        boolean hasAbilityUpdate() {
            return ability1 != 0 || ability2 != 0 || ability3 != 0;
        }

        boolean hasStatsUpdate() {
            return hp != 0 || attack != 0 || defense != 0 || spatk != 0 || spdef != 0 || speed != 0;
        }

        boolean hasExpYieldUpdate() {
            return expYield != 0;
        }

        boolean hasEVYieldUpdate() {
            return hpEVs != 0 || attackEVs != 0 || defenseEVs != 0 || spatkEVs != 0 || spdefEVs != 0 || speedEVs != 0;
        }
    }

    ParagonLiteHandler(Gen5RomHandler romHandler, byte[] arm9, int battleOvlNumber, int trainerAIOvlNumber, Pokemon[] pokes, Move[] moves,
                       List<String> battleEventStrings, List<String> abilityNames, List<String> abilityDescriptions,
                       List<String> abilityExplanations, List<String> moveNames, List<String> moveDescriptions) {
        this.romHandler = romHandler;
        
        this.arm9 = arm9;

        addressMap = new HashMap<>();
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("paragonlite/offsets.tsv"), "UTF-8");

            int gameIndex = romHandler.getGen5GameIndex();
            if (gameIndex < 0 || gameIndex > 3)
                throw new RuntimeException();

            // Skip header
            sc.nextLine();

            int namespaceColumn = 0;
            int funcNameColumn = 1;
            int addressStartColumn = 2;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] values = line.split("\t");

                String namespace = values[namespaceColumn].toLowerCase();
                addressMap.putIfAbsent(namespace, new HashMap<>());
                Map<String, Integer> namespaceFunctionAddressMap = addressMap.get(namespace);

                String funcName = values[funcNameColumn].toLowerCase();
                String offsetStr = values[addressStartColumn + gameIndex].substring(2);
                int address = Integer.parseUnsignedInt(offsetStr, 16);

                namespaceFunctionAddressMap.put(funcName, address);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            byte[] battleOvlData = romHandler.readOverlay(battleOvlNumber);
            int battleOvlAddress = romHandler.getOverlayAddress(battleOvlNumber);
            battleOvl = new ParagonLiteOverlay(battleOvlNumber, battleOvlData, battleOvlAddress);

            byte[] trainerAIOvlData = romHandler.readOverlay(trainerAIOvlNumber);
            int trainerAIOvlAddress = romHandler.getOverlayAddress(trainerAIOvlNumber);
            trainerAIOvl = new ParagonLiteOverlay(trainerAIOvlNumber, trainerAIOvlData, trainerAIOvlAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        referenceCounts = new HashMap<>();

        this.pokes = pokes;
        this.moves = moves;

        this.battleEventStrings = battleEventStrings;

        this.abilityNames = abilityNames;
        this.abilityDescriptions = abilityDescriptions;
        this.abilityExplanations = abilityExplanations;

        this.moveNames = moveNames;
        this.moveDescriptions = moveDescriptions;

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
        battleOvl.save(romHandler);
        trainerAIOvl.save(romHandler);
    }

    public void setBattleEventStrings() {
        // Limber
        /* 1159 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000's Speed\\xFFFEwas not lowered!");
        /* 1160 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000's Speed\\xFFFEwas not lowered!");
        /* 1161 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000's Speed\\xFFFEwas not lowered!");

        // Plus
        /* 1162 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a positive charge!");
        /* 1163 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a positive charge!");
        /* 1164 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a positive charge!");

        // Minus
        /* 1165 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a negative charge!");
        /* 1166 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a negative charge!");
        /* 1167 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 is overflowing\\xFFFEwith a negative charge!");

        // Super Luck
        /* 1168 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 is feeling lucky!");
        /* 1169 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 is feeling lucky!");
        /* 1170 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 is feeling lucky!");

        // Huge Power
        /* 1171 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 is flexing\\xFFFEits muscles!");
        /* 1172 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 is flexing\\xFFFEits muscles!");
        /* 1173 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 is flexing\\xFFFEits muscles!");

        // Pure Power
        /* 1174 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 is focusing\\xFFFEits mind!");
        /* 1175 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 is focusing\\xFFFEits mind!");
        /* 1176 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 is focusing\\xFFFEits mind!");

        // Magnet Pull
        /* 1177 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 is generating\\xFFFEa magnetic field!");
        /* 1178 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 is generating\\xFFFEa magnetic field!");
        /* 1179 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 is generating\\xFFFEa magnetic field!");

        // Shadow Tag
        /* 1180 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");
        /* 1181 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");
        /* 1182 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 steps\\xFFFEon shadows!");

        // Arena Trap
        /* 1183 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");
        /* 1184 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");
        /* 1185 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 dug\\xFFFEa pit trap!");

        // Wonder Guard
        /* 1186 */
        battleEventStrings.add("\uF000Ă\\x0001\\x0000 is cloaked in\\xFFFEa mysterious power!");
        /* 1187 */
        battleEventStrings.add("The wild \uF000Ă\\x0001\\x0000 is cloaked in\\xFFFEa mysterious power!");
        /* 1188 */
        battleEventStrings.add("The foe's \uF000Ă\\x0001\\x0000 is cloaked in\\xFFFEa mysterious power!");
    }

    public void setCritRatio() {
        int critChanceAddress = battleOvl.find(Gen5Constants.critChanceLocator);
        byte[] critChanceData = new byte[]{24, 8, 2, 1, 1};
        battleOvl.writeBytes(critChanceAddress, critChanceData);
    }

    public void setCritDamage() {
        try {
            int critLogicAddress = battleOvl.find(Gen5Constants.critLogicLocator);
            Scanner sc = new Scanner(FileFunctions.openConfig("paragonlite/crit.s"), "UTF-8");
            List<String> lines = new ArrayList<>();
            while (sc.hasNextLine()) {
                lines.add(sc.nextLine());
            }

            ArmParser armParser = new ArmParser(addressMap);
            byte[] critLogicData = armParser.parse(lines);

            battleOvl.writeBytes(critLogicAddress, critLogicData);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPokemonData() {
        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("paragonlite/pokes.ini"), "UTF-8");

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
                        pokeUpdate.ability2 -= poke.ability1;
                        pokeUpdate.ability3 += newAbility - poke.ability3;

                        poke.ability1 = newAbility;
                        poke.ability2 = 0;
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
                        if (!abilityStrs[0].isEmpty() && newAbility1 == 0) {
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        try {
            // Battle Overlay

            // Normally after a move score increment is added to the running total, we max() it with 0. This removes that,
            // allowing move scores to go negative during the process.
            int testAddress = trainerAIOvl.find("002801DA");
            trainerAIOvl.writeByte(testAddress + 3, 0xD1);

            // Trainer AI Scripts NARC
            NARCArchive narc = romHandler.readNARC(trainerAIScriptNarcPath);

            // file15 - Sets all move scores to negative
            narc.files.add(new byte[]{
                    (byte) 0x51, (byte) 0x00,
                    (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x04, (byte) 0x00,
                    (byte) 0xF6, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    (byte) 0x4D, (byte) 0x00,
                    (byte) 0x04, (byte) 0x00,
                    (byte) 0x0A, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x4D, (byte) 0x00,
            });

            romHandler.writeNARC(trainerAIScriptNarcPath, narc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTrainers() {
        List<Trainer> trainers = romHandler.getTrainers();
    }

    public void setAbilities() {
        int newAbilityCount = 17;

        // Move AbilityList
        relocateAbilityListRamAddress(newAbilityCount);
        cacheAbilityEventReferences();

        List<String> newStrs = Arrays.asList(new String[newAbilityCount]);
        abilityNames.addAll(newStrs);
        abilityDescriptions.addAll(newStrs);
        abilityExplanations.addAll(165, newStrs);

        // #007 Limber (+ no speed drop)
        setLimber();

        // #012 Oblivious
        setOblivious();

        // #014 Compoundeyes -> Compound Eyes
        setCompoundEyes();

        // #017 Immunity (+ Poison-type immunity)
        setImmunity();

        // #023 Shadow Tag
        setShadowTag();

        // #025 Wonder Guard
        setWonderGuard();

        // #031 Lightningrod -> Lightning Rod
        setLightningRod();

        // #037 Huge Power (1.5x Attack)
        setHugePower();

        // #040 Magma Armor (Water/Ground resist + 10% chance to burn on contact)
        setMagmaArmor();

        // #042 Magnet Pull
        setMagnetPull();

        // #045 Sand Stream (+ no sandstorm damage)
        setSandStream();

        // #055 Hustle (Moves with BP 60 or under gain +1 priority)
        setHustle();

        // #057 Plus (4/3 ally Sp. Atk)
        setPlus();

        // #058 Minus (4/3 ally Sp. Def)
        setMinus();

        // #071 Arena Trap
        setArenaTrap();

        // #072 Vital Spirit (boosts Sp. Def on hit)
        setVitalSpirit();

        // #074 Pure Power (1.5x Sp. Atk)
        setPurePower();

        // #079 Rivalry (1.0x opposite gender, 1.2x same gender)
        setRivalry();
        
        // #083 Anger Point (Boost Attack on miss, crit, or flinch)
        setAngerPoint();

        // #089 Iron Fist (1.2x -> 1.3x)
        setIronFist();

        // #091 Adaptability -> Specialized
        setSpecialized();

        // #094 Solar Power (1.5x -> 1.3x Sp. Atk; 1/8 -> 1/10 HP per turn)
        setSolarPower();

        // #102 Leaf Guard (2/3x damage received in sun)
        setLeafGuard();

        // #105 Super Luck
        setSuperLuck();

        // #115 Ice Body (+ Ice-type immunity)
        setIceBody();

        // #117 Snow Warning (+ no hail damage)
        setSnowWarning();

        // #119 Frisk -> X-ray Vision
        setXrayVision();

        // #132 Friend Guard (25% -> 20% reduction)
        setFriendGuard();

        // #134 Heavy Metal (+ 1.2x Defense, 0.9x Speed)
        setHeavyMetal();

        // #135 Light Metal (+ 0.9x Defense, 1.2x Speed)
        setLightMetal();

        // #142 Overcoat (+ immunity to spore moves)
        setOvercoat();

        // #154 Justified (+ immunity to Dark-type moves)
        setJustified();

        // #157 Sap Sipper -> Herbivore
        setHerbivore();
        
        // #163 Turbo Blaze (Fire-type attacks are always effective)
//        setTurboblaze();


        // NEW ABILITIES...

        // #165 Heavy Wing
        addHeavyWing();

        // #166 Adaptability (no STAB, 1.3x for all moves)
        addAdaptability();

        // #167 Insectivore
        addInsectivore();

        // #168 Slush Rush
        addSlushRush();

        // #169 Prestige
        addPrestige();

        // #170 Lucky Foot
        addLuckyFoot();

        // #171 Triage
        addTriage();

        // #172 Competitive
        addCompetitive();

        // #173 Strong Jaw
        addStrongJaw();

        // #174 Stamina
        addStamina();

        // #175 Assimilate
        addAssimilate();

        // #176 Sharpness
        addSharpness();

        // #177 Wind Rider
        addWindRider();

        // #178 Refrigerate
        addRefrigerate();

        // #179 Refrigerate
        addPixilate();

        // #180 Aerilate
        addAerilate();

        // #181 Galvanize
        addGalvanize();

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
    }

    private void setLimber() {
        int number = Abilities.limber;

        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainStatus),
                new AbilityEventHandler(Gen5BattleEventType.unknown67),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.unknown02),
                new AbilityEventHandler(Gen5BattleEventType.unknown5b, "limber_speed"),
                new AbilityEventHandler(Gen5BattleEventType.unknown5c, "limber_speed_message"));
    }

    private void setOblivious() {
        int number = Abilities.oblivious;

        // Description
        String description = abilityDescriptions.get(number);

        // Explanation
        String explanation = abilityExplanations.get(number);

        // Data
        setAbilityData(number,
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
        String explanation = abilityExplanations.get(number)
                .replace("Compoundeyes", "Compound Eyes");
        abilityExplanations.set(number, explanation);
    }

    private void setImmunity() {
        int number = Abilities.immunity;
        abilityUpdates.put(number, "Prevents the poison status condition; NEW: Unaffected to Poison-type moves");

        // Data
        setAbilityData(number,
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
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreSwitch),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "shadow_tag_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "shadow_tag_message"));
    }

    private void setWonderGuard() {
        int number = Abilities.wonderGuard;

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreMove),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "wonder_guard_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "wonder_guard_message"));
    }

    private void setLightningRod() {
        int number = Abilities.lightningRod;

        // Name
        abilityNames.set(number, "Lightning Rod");

        // Explanation
        String explanation = abilityExplanations.get(number)
                .replace("Lightningrod", "Lightning Rod");
        abilityExplanations.set(number, explanation);
    }

    private void setHugePower() {
        int number = Abilities.hugePower;

        // Explanation
        String explanation = "Huge Power, huh...\uF000븁\\x0000\\xFFFEThis Ability increases a Pokémon's\\xFFFEAttack stat by half.\uF000븁\\x0000\n";
        abilityExplanations.set(number, explanation);

        // Data
        setAbilityData(number,
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
        String explanation = "Magma Armor, huh...\uF000븁\\x0000\\xFFFEThis Ability halves damage from\\xFFFEWater- and Ground-type moves.\uF000븁\\x0000It also has a small chance to inflict\\xFFFEthe burned status condition\uF000븀\\x0000\\xFFFEwhen hit with a direct attack.\uF000븁\\x0000\\xFFFEWhat's more...\uF000븁\\x0000\\xFFFEIt makes Eggs in your party hatch faster.\uF000븁\\x0000\n";
        abilityExplanations.set(number, explanation);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onHit, "magma_armor_burn"),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "magma_armor_resist"));
    }

    private void setMagnetPull() {
        int number = Abilities.magnetPull;

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreSwitch),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "magnet_pull_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "magnet_pull_message"));
    }

    private void setSandStream() {
        int number = Abilities.sandStream;

        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "sand_stream_no_damage"));
    }

    private void setHustle() {
        int number = Abilities.hustle;
        abilityUpdates.put(number, "Increases Speed by 1.2x; decreases Attack and Sp. Atk to 0.9x");

        // Description
        String description = abilityDescriptions.get(number)
                .replace("Attack", "Speed")
                .replace("accuracy", "power");
        abilityDescriptions.set(number, description);

        // Explanation
        String explanation = "Hustle, huh...\uF000븁\\x0000\\xFFFEThis Ability raises the Pokémon's\\xFFFEAttack but lowers its accuracy.\uF000븁\\x0000\\xFFFEWhat's more...\uF000븁\\x0000\\xFFFEIt raises the chance to encounter\\xFFFEhigh-level wild Pokémon\uF000븀\\x0000\\xFFFEwhen the leading party member has it.\uF000븁\\x0000";
        abilityExplanations.set(number, explanation);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetPriority, "hustle"));
    }

    private void setPlus() {
        int number = Abilities.plus;

        // Description
        abilityDescriptions.set(number, "Boosts the Sp. Atk stat\\xFFFFof allies.");

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "plus_spatk"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "plus_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "plus_message"));
    }

    private void setMinus() {
        int number = Abilities.minus;

        // Description
        abilityDescriptions.set(number, "Boosts the Sp. Def stat\\xFFFFof allies.");

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStat, "minus_spdef"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "minus_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "minus_message"));
    }

    private void setArenaTrap() {
        int number = Abilities.arenaTrap;

        // Data
        setAbilityData(number,
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
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onHit, "vital_spirit"));
    }

    private void setPurePower() {
        int number = Abilities.purePower;

        // Description
        String description = abilityDescriptions.get(number)
                .replace("Attack", "Sp. Atk");
        abilityDescriptions.set(number, description);

        // Explanation
        String explanation = "Pure Power, huh...\uF000븁\\x0000\\xFFFEThis Ability increases a Pokémon's\\xFFFESp. Atk stat by half.\uF000븁\\x0000\n";
        abilityExplanations.set(number, explanation);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "pure_power"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter, "pure_power_message"),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility, "pure_power_message"));
    }

    private void setRivalry() {
        int number = Abilities.rivalry;

        // Name
        abilityExplanations.set(number, "Rivalry, huh...\uF000븁\\x0000\\xFFFEThis Ability raises the power of\\xFFFEthe Pokémon's move when the target is\uF000븀\\x0000\\xFFFEof the same gender.\uF000븁\\x0000");

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "rivalry"));
    }
    
    private void setAngerPoint() {
        int number = Abilities.angerPoint;
        
        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onHit, "anger_point_crit"),
                new AbilityEventHandler(Gen5BattleEventType.onFlinch, "anger_point_flinch"),
                new AbilityEventHandler(Gen5BattleEventType.OnMoveMiss, "anger_point_miss"));
    }

    private void setIronFist() {
        setAbilityData(Abilities.ironFist,
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "iron_fist"));
    }

    private void setSpecialized() {
        int number = ParagonLiteAbilities.specialized;

        // Name
        abilityNames.set(number, "Specialized");

        // Explanation
        String explanation = abilityExplanations.get(Abilities.adaptability)
                .replace("Adaptability", "Specialized");
        abilityExplanations.set(number, explanation);
    }

    private void setSolarPower() {
        int number = Abilities.solarPower;

        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "solar_power_weather"),
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "solar_power_spatk_boost"));
    }

    private void setLeafGuard() {
        int number = Abilities.leafGuard;

        // Description
        String description = "Reduces damage in\\xFFFEsunny weather.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "leaf_guard"));
    }

    private void setSuperLuck() {
        int number = Abilities.superLuck;

        // Data
        setAbilityData(number,
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
        String explanation = abilityExplanations.get(number);
        abilityExplanations.set(number, explanation);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onWeather),
                new AbilityEventHandler(Gen5BattleEventType.onPreMove, "ice_body_immunity"));
    }

    private void setSnowWarning() {
        int number = Abilities.snowWarning;

        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.onWeather, "snow_warning_no_damage"));
    }

    private void setXrayVision() {
        int number = ParagonLiteAbilities.xrayVision;

        // Name
        abilityNames.set(number, "X-ray Vision");

        // Explanation
        String explanation = abilityExplanations.get(Abilities.frisk)
                .replace("Frisk", "X-ray Vision");
        abilityExplanations.set(number, explanation);

        battleEventStrings.set(439, "\uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
        battleEventStrings.set(440, "The wild \uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
        battleEventStrings.set(441, "The foe's \uF000Ă\\x0001\\x0000 scanned its\\xFFFEtarget and found one \uF000ĉ\\x0001\\x0001!");
    }

    private void setFriendGuard() {
        int number = Abilities.friendGuard;

        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "friend_guard"));
    }

    private void setHeavyMetal() {
        int number = Abilities.heavyMetal;

        // Description
        String description = "Boosts the Defense stat,\\xFFFEbut lowers the Speed stat.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStat, "heavy_metal_defense"),
                new AbilityEventHandler(Gen5BattleEventType.onGetSpeedStat, "heavy_metal_speed"));
    }

    private void setLightMetal() {
        int number = Abilities.lightMetal;

        // Description
        String description = "Boosts the Speed stat, but\\xFFFElowers the Defense stat.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDefendingStat, "light_metal_defense"),
                new AbilityEventHandler(Gen5BattleEventType.onGetSpeedStat, "light_metal_speed"));
    }

    private void setOvercoat() {
        int number = Abilities.overcoat;

        // Description
        String description = "Protects the Pokémon from\\xFFFEsand, hail and powder.";
        abilityDescriptions.set(number, description);

        // Data
        setAbilityData(number,
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
        String explanation = abilityExplanations.get(Abilities.sapSipper)
                .replace("Sap Sipper", "Justified")
                .replace("Grass", "Dark");
        abilityExplanations.set(number, explanation);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onPreMove, "justified"));
    }

    private void setHerbivore() {
        int number = ParagonLiteAbilities.herbivore;

        // Name
        abilityNames.set(number, "Herbivore");

        // Explanation
        String explanation = abilityExplanations.get(number)
                .replace("Sap Sipper", "Herbivore");
        abilityExplanations.set(number, explanation);
    }
    
    private void setTurboblaze() {
        int number = Abilities.turboblaze;
        
        // Description
        abilityDescriptions.set(number, "Fire-type moves\\xFFFEare always effective.");
        
        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onPokeEnter),
                new AbilityEventHandler(Gen5BattleEventType.onPokeGainAbility),
                new AbilityEventHandler(Gen5BattleEventType.unknown03, "turboblaze"),
                new AbilityEventHandler(Gen5BattleEventType.unknown04),
                new AbilityEventHandler(Gen5BattleEventType.unknown6a));
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
        String explanation = abilityExplanations.get(Abilities.ironFist)
                .replace("Iron Fist", "Heavy Wing")
                .replace("moves that punch", "Flying-type moves");
        abilityExplanations.set(number, explanation);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetAttackingStat, "heavy_wing"));
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
        String explanation = "Adaptability, huh...\uF000븁\\x0000\\xFFFEThis ability gives a power boost to\\xFFFEall moves, even if the move doesn't\uF000븀\\x0000\\xFFFEmatch the Pokémon's type.\uF000븁\\x0000";
        abilityExplanations.set(number, explanation);

        // Data
        setAbilityData(number,
                new AbilityEventHandler(Gen5BattleEventType.onGetDamage, "adaptability"));
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
        String explanation = abilityExplanations.get(Abilities.waterAbsorb)
                .replace("Water Absorb", "Insectivore")
                .replace("Water", "Bug");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index, new AbilityEventHandler(Gen5BattleEventType.onPreMove, "insectivore"));
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
        String explanation = abilityExplanations.get(Abilities.snowCloak)
                .replace("Snow Cloak", "Slush Rush")
                .replace("evasiveness", "Speed");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
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
        String explanation = abilityExplanations.get(Abilities.moxie)
                .replace("Moxie", "Prestige")
                .replace("Attack", "Sp. Atk");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onPostHit, "prestige"));
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
        String explanation = abilityExplanations.get(Abilities.ironFist)
                .replace("Iron Fist", "Lucky Foot")
                .replace("punch", "kick");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "lucky_foot"));
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
        String explanation = abilityExplanations.get(Abilities.prankster)
                .replace("Prankster", "Triage")
                .replace("status moves", "moves that heal");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetPriority, "triage"));
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
        String explanation = abilityExplanations.get(Abilities.defiant)
                .replace("Defiant", "Competitive")
                .replace("Attack", "Sp. Atk");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onPostStatChange, "competitive"));
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
        String explanation = abilityExplanations.get(Abilities.ironFist)
                .replace("Iron Fist", "Strong Jaw")
                .replace("moves that punch", "moves that bite");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "strong_jaw"));
    }

    private void addStamina() {
        int index = ParagonLiteAbilities.stamina;

        // Name
        abilityNames.set(index, "Stamina");

        // Description
        abilityDescriptions.set(index, "Boosts the Defense stat\\xFFFEwhen hit by an attack.");

        // TODO: Explanation
        abilityExplanations.set(index, "A");

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onHit, "stamina"));
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
        String explanation = abilityExplanations.get(Abilities.sapSipper)
                .replace("Sap Sipper", "Assimilate")
                .replace("Grass", "Psychic")
                .replace("Attack", "Sp. Atk");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onPreMove, "assimilate"));
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
        String explanation = abilityExplanations.get(Abilities.ironFist)
                .replace("Iron Fist", "Sharpness")
                .replace("moves that punch", "moves that slice");
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "sharpness"));
    }

    private void addWindRider() {
        int index = ParagonLiteAbilities.windRider;

        // Name
        abilityNames.set(index, "Wind Rider");

        // Description
        String description = "Boosts Attack when hit\\xFFFEby a wind move.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        String explanation = "";
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onPreMove, "wind_rider"));
    }

    private void addRefrigerate() {
        int index = ParagonLiteAbilities.refrigerate;

        // Name
        abilityNames.set(index, "Refrigerate");

        // Description
        String description = "Normal-type moves become\\xFFFEIce-type moves.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        String explanation = "";
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "refrigerate_type"),
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "common_move_type_change_power"));
    }

    private void addPixilate() {
        int index = ParagonLiteAbilities.pixilate;

        // Name
        abilityNames.set(index, "Pixilate");

        // Description
        String description = "Normal-type moves become\\xFFFEFairy-type moves.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        String explanation = "";
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "pixilate_type"),
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "common_move_type_change_power"));
    }

    private void addAerilate() {
        int index = ParagonLiteAbilities.aerilate;

        // Name
        abilityNames.set(index, "Aerilate");

        // Description
        String description = "Normal-type moves become\\xFFFEFlying-type moves.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        String explanation = "";
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "aerilate_type"),
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "common_move_type_change_power"));
    }

    private void addGalvanize() {
        int index = ParagonLiteAbilities.galvanize;

        // Name
        abilityNames.set(index, "Galvanize");

        // Description
        String description = "Normal-type moves become\\xFFFEElectric-type moves.";
        abilityDescriptions.set(index, description);

        // TODO: Explanation
        String explanation = "";
        abilityExplanations.set(index, explanation);

        // Data
        setAbilityData(index,
                new AbilityEventHandler(Gen5BattleEventType.onGetMoveType, "galvanize_type"),
                new AbilityEventHandler(Gen5BattleEventType.OnGetMovePower, "common_move_type_change_power"));
    }

    private void setLowHpTypeBoostAbility() {
        int ramAddress = battleOvl.find("F8 B5 06 1C 03 20 0D 1C 14 1C 03 27");

        try {
            Scanner sc = new Scanner(FileFunctions.openConfig("paragonlite/eventhandlers/ability/low_hp_type_boost.s"), "UTF-8");

            List<String> lines = new ArrayList<>();
            while (sc.hasNextLine())
                lines.add(sc.nextLine());

            ArmParser armParser = new ArmParser(addressMap);
            byte[] data = armParser.parse(lines, ramAddress);

            int maxSize = 72;
            if (data.length > maxSize)
                throw new RuntimeException("Exceeded size!");

            battleOvl.writeBytes(ramAddress, data);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMoves() {
        // Kick Moves
        moves[Moves.stomp].isCustomKickMove = true;
        moves[Moves.doubleKick].isCustomKickMove = true;
        moves[Moves.jumpKick].isCustomKickMove = true;
        moves[Moves.rollingKick].isCustomKickMove = true;
        moves[Moves.lowKick].isCustomKickMove = true;
        moves[Moves.highJumpKick].isCustomKickMove = true;
        moves[Moves.tripleKick].isCustomKickMove = true;
        moves[Moves.blazeKick].isCustomKickMove = true;
        moves[Moves.lowSweep].isCustomKickMove = true;

        // Bite Moves
        moves[Moves.bite].isCustomBiteMove = true;
        moves[Moves.hyperFang].isCustomBiteMove = true;
        moves[Moves.crunch].isCustomBiteMove = true;
        moves[Moves.poisonFang].isCustomBiteMove = true;
        moves[Moves.thunderFang].isCustomBiteMove = true;
        moves[Moves.iceFang].isCustomBiteMove = true;
        moves[Moves.fireFang].isCustomBiteMove = true;

        // Slice Moves
        moves[Moves.cut].isCustomSliceMove = true;
        moves[Moves.razorLeaf].isCustomSliceMove = true;
        moves[Moves.slash].isCustomSliceMove = true;
        moves[Moves.furyCutter].isCustomSliceMove = true;
        moves[Moves.airCutter].isCustomSliceMove = true;
        moves[Moves.aerialAce].isCustomSliceMove = true;
        moves[Moves.airSlash].isCustomSliceMove = true;
        moves[Moves.crossPoison].isCustomSliceMove = true;
        moves[Moves.leafBlade].isCustomSliceMove = true;
        moves[Moves.nightSlash].isCustomSliceMove = true;
        moves[Moves.xScissor].isCustomSliceMove = true;
        moves[Moves.psychoCut].isCustomSliceMove = true;
        moves[Moves.sacredSword].isCustomSliceMove = true;
        moves[Moves.razorShell].isCustomSliceMove = true;
        moves[Moves.secretSword].isCustomSliceMove = true;

        // Triage
        for (int i = 0; i <= Gen5Constants.moveCount; ++i) {
            if (moves[i] == null)
                continue;

            if (moves[i].isHealMove || moves[i].recoil > 0)
                moves[i].isCustomTriageMove = true;
        }

        // Powder moves
        moves[Moves.poisonPowder].isCustomPowderMove = true;
        moves[Moves.stunSpore].isCustomPowderMove = true;
        moves[Moves.sleepPowder].isCustomPowderMove = true;
        moves[Moves.spore].isCustomPowderMove = true;
        moves[Moves.cottonSpore].isCustomPowderMove = true;
        moves[Moves.ragePowder].isCustomPowderMove = true;

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

        // #014 Swords Dance
        moves[Moves.swordsDance].target = MoveTarget.USER_OR_ALLY;

        // #112 Barrier
        moves[Moves.barrier].target = MoveTarget.USER_OR_ALLY;

        // #135 Soft-Boiled
        moves[Moves.softBoiled].target = MoveTarget.USER_OR_ALLY;

        // #208 Milk Drink
        moves[Moves.milkDrink].target = MoveTarget.USER_OR_ALLY;

//        // #237 Hidden Power (60 BP)
//        moves[Moves.hiddenPower].power = 60;
//        setMoveData(Moves.hiddenPower, new MoveEventHandler(Gen5BattleEventType.onGetMoveType));

        // #336 Howl
        moves[Moves.howl].target = MoveTarget.USER_OR_ALLY;

        // #349 Swords Dance
        moves[Moves.dragonDance].target = MoveTarget.USER_OR_ALLY;

        // #455 Defend Order
        moves[Moves.defendOrder].target = MoveTarget.USER_OR_ALLY;

        // #456 Heal Order
        moves[Moves.healOrder].target = MoveTarget.USER_OR_ALLY;

        // #468 Hone Claws
        moves[Moves.honeClaws].target = MoveTarget.USER_OR_ALLY;

        // #483 Quiver Dance
        moves[Moves.quiverDance].target = MoveTarget.USER_OR_ALLY;

        // #508 Shift Gear
        moves[Moves.shiftGear].target = MoveTarget.USER_OR_ALLY;
    }

    public void test() {
        disableRandomness();

        List<Trainer> trainers = romHandler.getTrainers();

        pokes[Species.minun].ability1 = Abilities.flashFire;
        
        for (Trainer tr : trainers) {
            tr.setPokemonHaveCustomMoves(true);
            tr.setPokemonHaveItems(true);

            TrainerPokemon poke1 = tr.pokemon.get(0);
            poke1.pokemon = romHandler.getPokemon().get(Species.minun);
            poke1.level = 15;
            poke1.abilitySlot = 1;
            poke1.moves = new int[]{Moves.recover, 0, 0, 0};
            poke1.strength = 255;
            poke1.heldItem = Items.brightPowder;

            if (tr.pokemon.size() == 1) {
                tr.pokemon.add(tr.pokemon.get(0).copy());
            }

            TrainerPokemon poke2 = tr.pokemon.get(1);
            poke2.pokemon = romHandler.getPokemon().get(Species.minun);
            poke2.level = 15;
            poke2.abilitySlot = 1;
            poke2.moves = new int[]{Moves.recover, 0, 0, 0};
            poke2.strength = 255;
            poke2.heldItem = Items.brightPowder;
        }

        romHandler.setTrainers(trainers, false, true);
    }

    private void setDebugMode() {
        int arm9Address = 0x02004000;

        int dsAddress = 0x0207AC88 - arm9Address;
        int dsiAddress = 0x0207AC84 - arm9Address;

        int ds = romHandler.readLong(arm9, dsAddress);
        int dsi = romHandler.readLong(arm9, dsiAddress);

        romHandler.writeLong(arm9, dsAddress, ds | 0x02000000);
        romHandler.writeLong(arm9, dsiAddress, dsi | 0x02000000);
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
        int natureStatAdjustOffset = romHandler.find(arm9, "00 00 00 00 00 01 00 00 00 FF");
        int natureStatOffset = romHandler.find(arm9, "00 00 00 00 00 01 FF 00 00 00");
        for (int i = 0; i < 5 * 25; ++i) {
            arm9[natureStatAdjustOffset + i] = 0x00;
            arm9[natureStatOffset + i] = 0x00;
        }
    }

    private void relocateAbilityListRamAddress(int additionalCount) {
        Map<String, Integer> map = addressMap.get("battle");
        int oldAddress = getAbilityListAddress();

        int cmpInstructionAddress = map.get("Inst_AbilityEffectListCountCmp".toLowerCase());
        int oldCount = battleOvl.readUnsignedByte(cmpInstructionAddress);
        int newCount = oldCount + additionalCount;
        battleOvl.writeByte(cmpInstructionAddress, newCount);

        int effectListAddressDataAddress = map.get("Data_AbilityEffectListAddress".toLowerCase());
        int effectListAddress4DataAddress = map.get("Data_AbilityEffectListAddress4".toLowerCase());
        int newAddress = relocateObjectList(oldAddress, oldCount, newCount, effectListAddressDataAddress, effectListAddress4DataAddress);
        map.put("Data_AbilityEffectList".toLowerCase(), newAddress);
    }

    private void relocateMoveListRamAddress(int additionalCount) {
        Map<String, Integer> map = addressMap.get("battle");
        int oldAddress = getAbilityListAddress();

        int countAddress = map.get("Data_MoveEffectListCount".toLowerCase());
        int oldCount = battleOvl.readWord(countAddress);
        int newCount = oldCount + additionalCount;
        battleOvl.writeWord(countAddress, newCount);

        int effectListAddressDataAddress = map.get("Data_MoveEffectListAddress".toLowerCase());
        int effectListAddress4DataAddress = map.get("Data_MoveEffectListAddress4".toLowerCase());
        int newAddress = relocateObjectList(oldAddress, oldCount, newCount, effectListAddressDataAddress, effectListAddress4DataAddress);
        map.put("Data_AbilityEffectList".toLowerCase(), newAddress);
    }

    private int relocateObjectList(int oldAddress, int oldCount, int newCount, int effectListAddressDataAddress, int effectListAddress4DataAddress) {
        int oldSize = oldCount * 8;
        int newSize = newCount * 8;
        int newAddress = battleOvl.allocate(newSize);
        battleOvl.copyBytes(oldAddress, newAddress, oldSize);

        battleOvl.writeWord(effectListAddressDataAddress, newAddress);
        battleOvl.writeWord(effectListAddress4DataAddress, newAddress + 4);

        battleOvl.free(oldAddress, oldSize);

        return newAddress;
    }

    private void cacheAbilityEventReferences() {
        int abilityListAddress = getAbilityListAddress();
        int abilityListCount = getAbilityListCount();

        for (int i = 0; i < abilityListCount; ++i) {
            int abilityAddress = abilityListAddress + i * 8;
            int abilityNumber = battleOvl.readWord(abilityAddress);
            if (abilityNumber == 0)
                continue;

            int redirectorFuncAddress = battleOvl.readWord(abilityAddress + 4) - 1;
            addReference(redirectorFuncAddress);

            int redirectorCountSetAddress = getRedirectorCountSetAddress(redirectorFuncAddress);
            int redirectorEventListAddress = getRedirectorListReferenceAddress(redirectorFuncAddress);
            if (redirectorCountSetAddress < 0 || redirectorEventListAddress < 0)
                throw new RuntimeException();


            int eventListCount = battleOvl.readUnsignedByte(redirectorCountSetAddress);
            int eventListAddress = battleOvl.readWord(redirectorEventListAddress);
            addReference(eventListAddress);

            for (int j = 0; j < eventListCount; ++j) {
                int address = eventListAddress + j * 8;
                int funcAddress = battleOvl.readWord(address + 4) - 1;
                addReference(funcAddress);
            }
        }
    }

    private void addReference(int address) {
        referenceCounts.put(address, referenceCounts.getOrDefault(address, 0) + 1);
    }

    private int removeReference(int address) {
        int current = referenceCounts.getOrDefault(address, 0);
        if (current == 0)
            return 0;

        int newCount = current - 1;
        if (newCount <= 0)
            referenceCounts.remove(address);
        else
            referenceCounts.put(address, newCount);

        return newCount;
    }

    private void removeRedirectorReference(int address) {
        if (removeReference(address) > 0)
            return;

        int redirectorCountSetAddress = getRedirectorCountSetAddress(address);
        int eventListCount = battleOvl.readUnsignedByte(redirectorCountSetAddress);
        int redirectorEventListAddress = getRedirectorListReferenceAddress(address);
        int eventListAddress = battleOvl.readWord(redirectorEventListAddress);

        removeEventListReference(eventListAddress, eventListCount);

        battleOvl.freeFunc(address);
    }

    private void removeEventListReference(int address, int eventListCount) {
        if (removeReference(address) > 0)
            return;

        for (int i = 0; i < eventListCount; ++i) {
            int funcAddress = battleOvl.readWord(address + i * 8 + 4) - 1;
            removeEventListFuncReference(funcAddress);
        }

        battleOvl.free(address, eventListCount * 8);
    }

    private void removeEventListFuncReference(int address) {
        if (removeReference(address) <= 0)
            battleOvl.freeFunc(address);
    }

    private int getAbilityListAddress() {
        String namespace = "Battle".toLowerCase();
        String funcName = "Data_AbilityEffectList".toLowerCase();
        return addressMap.get(namespace).get(funcName);
    }

    private int getMoveListAddress() {
        String namespace = "Battle".toLowerCase();
        String funcName = "Data_MoveEffectList".toLowerCase();
        return addressMap.get(namespace).get(funcName);
    }

    private int getRedirectorCountSetAddress(int funcAddress) {
        int returnValue = -1;

        int funcSize = battleOvl.getFuncSize(funcAddress);
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

        int funcSize = battleOvl.getFuncSize(funcAddress);
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

    private abstract class BattleEventHandler {
        int type;
        int address = -1;

        protected void setFromFuncName(int type, String fullFuncName) {
            this.type = type;

            if (fullFuncName.contains("::")) {
                address = getFuncAddress(fullFuncName);
                return;
            }

            String namespace = "custom_battle_event";
            addressMap.putIfAbsent(namespace, new HashMap<>());
            Map<String, Integer> customMap = addressMap.get(namespace);

            String funcNameKey = String.format("%s_%s", getFuncDirectory(), fullFuncName);
            if (!customMap.containsKey(funcNameKey)) {
                Scanner sc;
                try {
                    InputStream file = FileFunctions.openConfig(String.format("paragonlite/eventhandlers/%s/%s.s", getFuncDirectory(), fullFuncName));
                    sc = new Scanner(file, "UTF-8");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                List<String> lines = new ArrayList<>();
                while (sc.hasNextLine())
                    lines.add(sc.nextLine());

                ArmParser armParser = new ArmParser(addressMap);
                int funcAddress = battleOvl.writeArm(lines, armParser);
                customMap.put(funcNameKey, funcAddress);
            }

            address = customMap.get(funcNameKey);
        }

        protected abstract String getFuncDirectory();

        protected abstract int getObjectListAddress();

        protected abstract int getObjectListCount();
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

        @Override
        protected int getObjectListAddress() {
            return getAbilityListAddress();
        }

        @Override
        protected int getObjectListCount() {
            return getAbilityListCount();
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

        @Override
        protected int getObjectListAddress() {
            return getMoveListAddress();
        }

        @Override
        protected int getObjectListCount() {
            return getMoveListCount();
        }
    }

    private void setBattleObject(int number, int index, int objectListAddress, BattleEventHandler... eventHandlers) {
        int eventHandlerListSize = eventHandlers.length * 8;

        int eventHandlerListAddress = battleOvl.allocate(eventHandlerListSize);
        addReference(eventHandlerListAddress);

        List<String> lines = Arrays.asList(
                "mov r1, #" + eventHandlerListSize,
                "str r1, [r0]",
                "ldr r0, =" + eventHandlerListAddress,
                "bx lr"
        );

        ArmParser armParser = new ArmParser(addressMap);
        int redirectorFuncAddress = battleOvl.writeArm(lines, armParser);
        addReference(redirectorFuncAddress);

        int objectDataAddress = objectListAddress + index * 8;
        int oldRedirectorFuncAddress = battleOvl.readWord(objectDataAddress + 4) - 1;
        boolean isNew = oldRedirectorFuncAddress < 0;

        // Update existing event handlers
        if (!isNew) {
            int oldEventHandlerListReferenceAddress = getRedirectorListReferenceAddress(oldRedirectorFuncAddress);
            int oldEventHandlerListAddress = battleOvl.readWord(oldEventHandlerListReferenceAddress);

            int oldCountSetAddress = getRedirectorCountSetAddress(oldRedirectorFuncAddress);
            int oldEventHandlerListCount = battleOvl.readUnsignedByte(oldCountSetAddress);

            Map<Integer, Integer> existingEventHandlers = new HashMap<>(oldEventHandlerListCount);
            for (int i = 0; i < oldEventHandlerListCount; ++i) {
                int eventHandlerListElementAddress = oldEventHandlerListAddress + i * 8;
                int oldType = battleOvl.readWord(eventHandlerListElementAddress);
                int oldAddress = battleOvl.readWord(eventHandlerListElementAddress + 4) - 1;
                existingEventHandlers.put(oldType, oldAddress);
            }

            for (BattleEventHandler eventHandler : eventHandlers) {
                if (eventHandler.address < 0) {
                    eventHandler.address = existingEventHandlers.get(eventHandler.type);
                    addReference(eventHandler.address);
                }
            }
        }

        // Write object data
        battleOvl.writeWord(objectDataAddress, number);
        battleOvl.writeWord(objectDataAddress + 4, redirectorFuncAddress + 1);

        int eventListCountSetAddress = getRedirectorCountSetAddress(redirectorFuncAddress);
        int eventHandlerListReferenceAddress = getRedirectorListReferenceAddress(redirectorFuncAddress);

        // Count
        battleOvl.writeByte(eventListCountSetAddress, (byte) (eventHandlers.length & 0xFF));

        // Address
        battleOvl.writeWord(eventHandlerListReferenceAddress, eventHandlerListAddress);

        // Write entries
        for (int i = 0; i < eventHandlers.length; ++i) {
            BattleEventHandler event = eventHandlers[i];
            battleOvl.writeWord(eventHandlerListAddress + i * 8, event.type);
            battleOvl.writeWord(eventHandlerListAddress + i * 8 + 4, event.address + 1);
        }

        if (!isNew)
            removeRedirectorReference(oldRedirectorFuncAddress);
    }

    private void setAbilityData(int abilityNumber, AbilityEventHandler... events) {
        int abilityListRamAddress = getAbilityListAddress();
        int abilityListCount = getAbilityListCount();
        int index = getBattleObjectIndex(abilityListRamAddress, abilityListCount, abilityNumber);
        setBattleObject(abilityNumber, index, abilityListRamAddress, events);
    }

    private void setMoveData(int moveNumber, MoveEventHandler... events) {
        int moveListAddress = getMoveListAddress();
        int moveListCount = getMoveListCount();
        int index = getBattleObjectIndex(moveListAddress, moveListCount, moveNumber);
        setBattleObject(moveNumber, index, moveListAddress, events);
    }

    private int getAbilityListCount() {
        int cmpInstructionAddress = addressMap.get("battle").get("Inst_AbilityEffectListCountCmp".toLowerCase());
        return battleOvl.readUnsignedByte(cmpInstructionAddress);
    }

    private int getMoveListCount() {
        int dataAddress = addressMap.get("battle").get("Data_MoveEffectListCount".toLowerCase());
        return battleOvl.readWord(dataAddress);
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
        return getFuncAddress(strs[0], strs[1]);
    }

    public int getFuncAddress(String namespace, String funcName) {
        addressMap.putIfAbsent(namespace, new HashMap<>());
        Map<String, Integer> namespaceMap = addressMap.get(namespace);

        if (!namespaceMap.containsKey(funcName))
            throw new RuntimeException(String.format("Could not find function \"%s::%s\".", namespace, funcName));

        return namespaceMap.get(funcName);
    }

    public void logUpdates(PrintStream log) {
        // Abilities
        for (int i = 0; i <= Abilities.teravolt; ++i) {
            String name = abilityNames.get(i);
            String oldName = oldAbilityNames.get(i);
            boolean updatedName = false;
            if (!Objects.equals(name, oldName)) {
                log.printf("#%3d %s >> %s%n", i, oldName, name);
                updatedName = true;
            }

            if (abilityUpdates.containsKey(i)) {
                if (!updatedName)
                    log.printf("#%3d %s%n", i, name);

                String abilityUpdate = abilityUpdates.get(i);
                log.println("- " + abilityUpdate);
            }

            log.println();
        }

        // Pokemon
        for (int i = 0; i <= pokes.length; ++i) {
            PokeUpdate pokeUpdate = pokeUpdates[i];
            if (!pokeUpdate.hasAnyUpdate())
                continue;

            Pokemon poke = pokes[i];
            log.println("====================");
            log.printf("#%3d %s%n", poke.number, poke.fullName());
            log.println("====================");

            if (pokeUpdate.hasTypeUpdate()) {
                log.println("Type:");
                Type oldType1 = Type.values()[poke.primaryType.ordinal() - pokeUpdate.type1];

                int oldType2Ordinal = (poke.secondaryType == null ? -1 : poke.secondaryType.ordinal()) - pokeUpdate.type2;
                Type oldType2 = oldType2Ordinal < 0 ? null : Type.values()[oldType2Ordinal];

                String oldTypeStr = oldType2 == null ? oldType1.camelCase() : (oldType1.camelCase() + " / " + oldType2.camelCase());
                log.printf("Old    %s%n", oldTypeStr);
                log.printf("New    %s%n", poke.getTypeString(true));
                log.println();
            }

            if (pokeUpdate.hasAbilityUpdate()) {
                log.println("Abilities:");

                String oldAbility1 = oldAbilityNames.get(poke.ability1 - pokeUpdate.ability1);
                String oldAbility2 = oldAbilityNames.get(poke.ability2 - pokeUpdate.ability2);
                String oldAbility3 = oldAbilityNames.get(poke.ability3 - pokeUpdate.ability3);

                log.printf("Old    1: %s / 2: %s / HA: %s%n", oldAbility1, oldAbility2, oldAbility3);
                log.printf("New    1: %s / 2: %s / HA: %s%n", abilityNames.get(poke.ability1), abilityNames.get(poke.ability2), abilityNames.get(poke.ability3));
            }
        }
    }

    private static int alignWord(int address) {
        return address & 0xFFFFFFFC;
    }
}
