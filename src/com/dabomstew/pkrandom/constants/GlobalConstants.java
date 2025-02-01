package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  GlobalConstants.java - constants that are relevant for multiple games --*/
/*--                         in the Pokemon series                          --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import com.dabomstew.pkrandom.pokemon.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalConstants {
    /* @formatter:off */

    public static final List<Integer> varyingPowerZMoves = Arrays.asList(
            Moves.breakneckBlitzPhysical, Moves.breakneckBlitzSpecial,
            Moves.allOutPummelingPhysical, Moves.allOutPummelingSpecial,
            Moves.supersonicSkystrikePhysical, Moves.supersonicSkystrikeSpecial,
            Moves.acidDownpourPhysical, Moves.acidDownpourSpecial,
            Moves.tectonicRagePhysical, Moves.tectonicRageSpecial,
            Moves.continentalCrushPhysical, Moves.continentalCrushSpecial,
            Moves.savageSpinOutPhysical, Moves.savageSpinOutSpecial,
            Moves.neverEndingNightmarePhysical, Moves.neverEndingNightmareSpecial,
            Moves.corkscrewCrashPhysical, Moves.corkscrewCrashSpecial,
            Moves.infernoOverdrivePhysical, Moves.infernoOverdriveSpecial,
            Moves.hydroVortexPhysical, Moves.hydroVortexSpecial,
            Moves.bloomDoomPhysical, Moves.bloomDoomSpecial,
            Moves.gigavoltHavocPhysical, Moves.gigavoltHavocSpecial,
            Moves.shatteredPsychePhysical, Moves.shatteredPsycheSpecial,
            Moves.subzeroSlammerPhysical, Moves.subzeroSlammerSpecial,
            Moves.devastatingDrakePhysical, Moves.devastatingDrakeSpecial,
            Moves.blackHoleEclipsePhysical, Moves.blackHoleEclipseSpecial,
            Moves.twinkleTacklePhysical, Moves.twinkleTackleSpecial);

    public static final List<Integer> fixedPowerZMoves = Arrays.asList(
            Moves.catastropika, Moves.sinisterArrowRaid, Moves.maliciousMoonsault, Moves.oceanicOperetta,
            Moves.guardianOfAlola, Moves.soulStealing7StarStrike, Moves.stokedSparksurfer, Moves.pulverizingPancake,
            Moves.extremeEvoboost, Moves.genesisSupernova, Moves.tenMillionVoltThunderbolt, Moves.lightThatBurnsTheSky,
            Moves.searingSunrazeSmash, Moves.menacingMoonrazeMaelstrom, Moves.letsSnuggleForever,
            Moves.splinteredStormshards, Moves.clangorousSoulblaze);

    public static final List<Integer> zMoves = Stream.concat(fixedPowerZMoves.stream(),
            varyingPowerZMoves.stream()).collect(Collectors.toList());

    public static Map<Integer,StatChange> getStatChanges(int generation) {
        Map<Integer,StatChange> map = new TreeMap<>();

        switch(generation) {
            case 6:
                map.put(Species.butterfree,new StatChange(Stat.SPATK.val,90));
                map.put(Species.beedrill,new StatChange(Stat.ATK.val,90));
                map.put(Species.pidgeot,new StatChange(Stat.SPEED.val,101));
                map.put(Species.pikachu,new StatChange(Stat.DEF.val | Stat.SPDEF.val,40, 50));
                map.put(Species.raichu,new StatChange(Stat.SPEED.val,110));
                map.put(Species.nidoqueen,new StatChange(Stat.ATK.val,92));
                map.put(Species.nidoking,new StatChange(Stat.ATK.val,102));
                map.put(Species.clefable,new StatChange(Stat.SPATK.val,95));
                map.put(Species.wigglytuff,new StatChange(Stat.SPATK.val,85));
                map.put(Species.vileplume,new StatChange(Stat.SPATK.val,110));
                map.put(Species.poliwrath,new StatChange(Stat.ATK.val,95));
                map.put(Species.alakazam,new StatChange(Stat.SPDEF.val,95));
                map.put(Species.victreebel,new StatChange(Stat.SPDEF.val,70));
                map.put(Species.golem,new StatChange(Stat.ATK.val,120));
                map.put(Species.ampharos,new StatChange(Stat.DEF.val,85));
                map.put(Species.bellossom,new StatChange(Stat.DEF.val,95));
                map.put(Species.azumarill,new StatChange(Stat.SPATK.val,60));
                map.put(Species.jumpluff,new StatChange(Stat.SPDEF.val,95));
                map.put(Species.beautifly,new StatChange(Stat.SPATK.val,100));
                map.put(Species.exploud,new StatChange(Stat.SPDEF.val,73));
                map.put(Species.staraptor,new StatChange(Stat.SPDEF.val,60));
                map.put(Species.roserade,new StatChange(Stat.DEF.val,65));
                map.put(Species.stoutland,new StatChange(Stat.ATK.val,110));
                map.put(Species.unfezant,new StatChange(Stat.ATK.val,115));
                map.put(Species.gigalith,new StatChange(Stat.SPDEF.val,80));
                map.put(Species.seismitoad,new StatChange(Stat.ATK.val,95));
                map.put(Species.leavanny,new StatChange(Stat.SPDEF.val,80));
                map.put(Species.scolipede,new StatChange(Stat.ATK.val,100));
                map.put(Species.krookodile,new StatChange(Stat.DEF.val,80));
                break;
            case 7:
                map.put(Species.arbok,new StatChange(Stat.ATK.val,95));
                map.put(Species.dugtrio,new StatChange(Stat.ATK.val,100));
                map.put(Species.farfetchd,new StatChange(Stat.ATK.val,90));
                map.put(Species.dodrio,new StatChange(Stat.SPEED.val,110));
                map.put(Species.electrode,new StatChange(Stat.SPEED.val,150));
                map.put(Species.exeggutor,new StatChange(Stat.SPDEF.val,75));
                map.put(Species.noctowl,new StatChange(Stat.SPATK.val,86));
                map.put(Species.ariados,new StatChange(Stat.SPDEF.val,70));
                map.put(Species.qwilfish,new StatChange(Stat.DEF.val,85));
                map.put(Species.magcargo,new StatChange(Stat.HP.val | Stat.SPATK.val,60,90));
                map.put(Species.corsola,new StatChange(Stat.HP.val | Stat.DEF.val | Stat.SPDEF.val,65,95,95));
                map.put(Species.mantine,new StatChange(Stat.HP.val,85));
                map.put(Species.swellow,new StatChange(Stat.SPATK.val,75));
                map.put(Species.pelipper,new StatChange(Stat.SPATK.val,95));
                map.put(Species.masquerain,new StatChange(Stat.SPATK.val | Stat.SPEED.val,100,80));
                map.put(Species.delcatty,new StatChange(Stat.SPEED.val,90));
                map.put(Species.volbeat,new StatChange(Stat.DEF.val | Stat.SPDEF.val,75,85));
                map.put(Species.illumise,new StatChange(Stat.DEF.val | Stat.SPDEF.val,75,85));
                map.put(Species.lunatone,new StatChange(Stat.HP.val,90));
                map.put(Species.solrock,new StatChange(Stat.HP.val,90));
                map.put(Species.chimecho,new StatChange(Stat.HP.val | Stat.DEF.val | Stat.SPDEF.val,75,80,90));
                map.put(Species.woobat,new StatChange(Stat.HP.val,65));
                map.put(Species.crustle,new StatChange(Stat.ATK.val,105));
                map.put(Species.beartic,new StatChange(Stat.ATK.val,130));
                map.put(Species.cryogonal,new StatChange(Stat.HP.val | Stat.DEF.val,80,50));
                break;
            case 8:
                map.put(Species.aegislash,new StatChange(Stat.DEF.val | Stat.SPDEF.val,140,140));
                break;
            case 9:
                map.put(Species.cresselia,new StatChange(Stat.DEF.val | Stat.SPDEF.val, 110,120));
                map.put(Species.zacian,new StatChange(Stat.ATK.val, 120));
                map.put(Species.zamazenta,new StatChange(Stat.ATK.val, 120));
                break;
        }
        return map;
    }

    /* @formatter:on */

    public static final List<Integer> xItems = Arrays.asList(Items.guardSpec, Items.direHit, Items.xAttack,
            Items.xDefense, Items.xSpeed, Items.xAccuracy, Items.xSpAtk, Items.xSpDef);

    public static final List<Integer> battleTrappingAbilities = Arrays.asList(Abilities.shadowTag, Abilities.magnetPull,
            Abilities.arenaTrap);

    public static final List<Integer> negativeAbilities = Arrays.asList(
            Abilities.defeatist, Abilities.slowStart, Abilities.truant, Abilities.klutz, Abilities.stall
    );

    public static final List<Integer> badAbilities = Arrays.asList(
            Abilities.forewarn, Abilities.honeyGather, Abilities.auraBreak, Abilities.receiver, Abilities.powerOfAlchemy
    );

    public static final List<Integer> doubleBattleAbilities = Arrays.asList(
            Abilities.friendGuard, Abilities.healer, Abilities.telepathy, Abilities.symbiosis,
            Abilities.battery,

            Abilities.plus, Abilities.minus,
            ParagonLiteAbilities.ripTide
    );

    public static final List<Integer> duplicateAbilities = Arrays.asList(
            /*Abilities.vitalSpirit,*/ Abilities.whiteSmoke, /*Abilities.purePower,*/ Abilities.shellArmor, Abilities.airLock,
            Abilities.solidRock, Abilities.ironBarbs, Abilities.turboblaze, Abilities.teravolt, Abilities.emergencyExit,
            Abilities.dazzling, Abilities.tanglingHair, Abilities.powerOfAlchemy, Abilities.fullMetalBody,
            Abilities.shadowShield, Abilities.prismArmor, Abilities.libero, Abilities.stalwart
    );

    public static final List<Integer> noPowerNonStatusMoves = Arrays.asList(
            Moves.guillotine, Moves.hornDrill, Moves.sonicBoom, Moves.lowKick, Moves.counter, Moves.seismicToss,
            Moves.dragonRage, Moves.fissure, Moves.nightShade, Moves.bide, Moves.psywave, Moves.superFang,
            Moves.flail, Moves.revenge, Moves.returnTheMoveNotTheKeyword, Moves.present, Moves.frustration,
            Moves.magnitude, Moves.mirrorCoat, Moves.beatUp, Moves.spitUp, Moves.sheerCold
    );

    public static final List<Integer> cannotBeObsoletedMoves = Arrays.asList(
            Moves.returnTheMoveNotTheKeyword, Moves.frustration, Moves.endeavor, Moves.flail, Moves.reversal,
            Moves.hiddenPower, Moves.storedPower, Moves.smellingSalts, Moves.fling, Moves.powerTrip, Moves.counter,
            Moves.mirrorCoat, Moves.superFang
    );

    public static final List<Integer> cannotObsoleteMoves = Arrays.asList(
            Moves.gearUp, Moves.magneticFlux, Moves.focusPunch, Moves.explosion, Moves.selfDestruct, Moves.geomancy,
            Moves.venomDrench
    );

    public static final List<Integer> doubleBattleMoves = Arrays.asList(
            Moves.followMe, Moves.helpingHand, Moves.ragePowder, Moves.afterYou, Moves.allySwitch, Moves.healPulse,
            Moves.quash, Moves.ionDeluge, Moves.matBlock, Moves.aromaticMist, Moves.electrify, Moves.instruct,
            Moves.spotlight, Moves.decorate, Moves.lifeDew, Moves.coaching
    );

    public static final List<Integer> uselessMoves = Arrays.asList(
            Moves.splash, Moves.celebrate, Moves.holdHands, Moves.teleport,
            Moves.reflectType       // the AI does not know how to use this move properly

    );

    public static final Set<Integer> bannedMoves = new HashSet<>(Arrays.asList(
            Moves.sandAttack, Moves.minimize, Moves.flash, Moves.sketch, Moves.pursuit, Moves.assist, Moves.darkVoid, Moves.allySwitch,
            Moves.returnTheMoveNotTheKeyword, Moves.frustration,
            
            Moves.meditate // TODO
    ));

    public static final List<Integer> tmBannedMoves = Arrays.asList(
            Moves.sandAttack, Moves.rage, Moves.teleport, Moves.mimic, Moves.minimize, Moves.smokescreen, Moves.transform, Moves.flash,
            Moves.splash, Moves.conversion, Moves.sketch, Moves.spiderWeb, Moves.mindReader, Moves.nightmare, Moves.snore, Moves.flail,
            Moves.conversion2, Moves.spite, Moves.destinyBond, Moves.perishSong, Moves.lockOn, Moves.endure, Moves.charm, Moves.falseSwipe,
            Moves.swagger, Moves.meanLook, Moves.sleepTalk, Moves.present, Moves.frustration, Moves.safeguard, Moves.sweetScent,
            Moves.psychUp, Moves.flatter, Moves.charge, Moves.trick, Moves.rolePlay, Moves.assist, Moves.ingrain, Moves.magicCoat,
            Moves.recycle, Moves.endeavor, Moves.skillSwap, Moves.imprison, Moves.refresh, Moves.grudge, Moves.snatch, Moves.camouflage,
            Moves.teeterDance, Moves.mudSport, Moves.odorSleuth, Moves.block, Moves.miracleEye, Moves.healingWish, Moves.embargo,
            Moves.trumpCard, Moves.powerTrick, Moves.copycat, Moves.powerSwap, Moves.guardSwap, Moves.lastResort, Moves.heartSwap,
            Moves.magnetRise, Moves.switcheroo, Moves.captivate, Moves.lunarDance, Moves.guardSplit, Moves.powerSplit, Moves.wonderRoom,
            Moves.telekinesis, Moves.magicRoom, Moves.entrainment, Moves.quash, Moves.reflectType, Moves.finalGambit, Moves.bestow
    );

    public static final List<Integer> requiresOtherMove = Arrays.asList(
            Moves.spitUp, Moves.swallow, Moves.dreamEater, Moves.nightmare
    );

    public static final int MIN_DAMAGING_MOVE_POWER = 60;

    public static final int HIGHEST_POKEMON_GEN = 9;

    // Eevee has 8 potential evolutions
    public static final int LARGEST_NUMBER_OF_SPLIT_EVOS = 8;
    
    public static final List<Integer> genStartNumbers = Arrays.asList(
            Species.bulbasaur,
            Species.chikorita,
            Species.treecko,
            Species.turtwig,
            Species.victini,
            Species.chespin,
            Species.rowlet,
            Species.grookey,
            Species.wyrdeer
    );
    
    public static final List<Integer> genEndNumbers = Arrays.asList(
            Species.mew,
            Species.celebi,
            Species.deoxys,
            Species.arceus,
            Species.genesect,
            Species.volcanion,
            Species.melmetal,
            Species.calyrex,
            Species.pecharunt
    );
}
