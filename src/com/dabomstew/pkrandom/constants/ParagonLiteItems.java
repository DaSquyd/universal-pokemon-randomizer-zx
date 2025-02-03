package com.dabomstew.pkrandom.constants;

/*----------------------------------------------------------------------------*/
/*--  Abilities.java - defines an index number constant for every Ability.  --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
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

import java.util.Set;

public class ParagonLiteItems {
    // 57 new
    public static final int /*0639*/ weaknessPolicy = 113;
    public static final int /*0640*/ assaultVest = 114;
    public static final int /*0644*/ pixiePlate = 115;
    public static final int /*0649*/ snowball = 120;
    public static final int /*0650*/ safetyGoggles = 121;
    public static final int /*0686*/ roseliBerry = 122;
    public static final int /*0687*/ keeBerry = 123;
    public static final int /*0688*/ marangaBerry = 124;
    public static final int /*0715*/ fairyGem = 125;
    public static final int /*0795*/ bottleCap = 126;
    public static final int /*0796*/ goldBottleCap = 127;
    public static final int /*----*/ rustyBottleCap = 128;
    public static final int /*0846*/ adrenalineOrb = 129;
    public static final int /*0849*/ iceStone = 130;
    public static final int /*0879*/ terrainExtender = 131;
    public static final int /*0880*/ protectivePads = 132;
    public static final int /*0881*/ electricSeed = 133;
    public static final int /*0882*/ psychicSeed = 426;
    public static final int /*0883*/ mistySeed = 427;
    public static final int /*0884*/ grassySeed = Items.apricornBox; // 468
    public static final int /*1118*/ throatSpray = Items.unownReport; // 469
    public static final int /*1119*/ ejectPack = Items.berryPots; // 470
    public static final int /*1120*/ heavyDutyBoots = Items.blueCard; // 472
    public static final int /*1121*/ blunderPolicy = Items.slowpokeTail; // 473
    public static final int /*1122*/ roomService = Items.cardKeyJohto; // 475
    public static final int /*1123*/ utilityUmbrella = Items.basementKeyJohto; // 476
    public static final int /*1231*/ lonelyMint = Items.squirtBottle; // 477
    public static final int /*1232*/ adamantMint = Items.redScale; // 478
    public static final int /*1233*/ naughtyMint = Items.lostItem; // 479
    public static final int /*1234*/ braveMint = Items.pass; // 480
    public static final int /*1235*/ boldMint = Items.machinePart; // 481
    public static final int /*1236*/ impishMint = Items.mysteryEgg; // 484
    public static final int /*1237*/ laxMint = Items.photoAlbum; // 501
    public static final int /*1238*/ relaxedMint = Items.gbSounds; // 502
    public static final int /*1239*/ modestMint = Items.dataCard01; // 505
    public static final int /*1240*/ mildMint = Items.dataCard02; // 506
    public static final int /*1241*/ rashMint = Items.dataCard03; // 507
    public static final int /*1242*/ quietMint = Items.dataCard04; // 508
    public static final int /*1243*/ calmMint = Items.dataCard05; // 509
    public static final int /*1244*/ gentleMint = Items.dataCard06; // 510
    public static final int /*1245*/ carefulMint = Items.dataCard07; // 511
    public static final int /*1246*/ sassyMint = Items.dataCard08; // 512
    public static final int /*1247*/ timidMint = Items.dataCard09; // 513
    public static final int /*1248*/ hastyMint = Items.dataCard10; // 514
    public static final int /*1249*/ jollyMint = Items.dataCard11; // 515
    public static final int /*1250*/ naiveMint = Items.dataCard12; // 516
    public static final int /*1251*/ seriousMint = Items.dataCard13; // 517
    public static final int /*1611*/ linkingCord = Items.dataCard14; // 518
    public static final int /*1780*/ blankPlate = Items.dataCard15; // 519
    public static final int /*1828*/ legendPlate = Items.dataCard16; // 520
    public static final int /*1881*/ abilityShield = Items.dataCard17; // 521
    public static final int /*1882*/ clearAmulet = Items.dataCard18; // 522
    public static final int /*1883*/ mirrorHerb = Items.dataCard19; // 523
    public static final int /*1884*/ punchingGlove = Items.dataCard20; // 524
    public static final int /*1885*/ covertCloak = Items.dataCard21; // 525
    public static final int /*1886*/ loadedDice = Items.dataCard22; // 526
    public static final int /*2401*/ fairyFeather = Items.dataCard23; // 527
    
    public static Set<Integer> getAllowed() {
        return Set.of(
                weaknessPolicy,
                assaultVest,
                pixiePlate,
                roseliBerry,
                fairyGem,
                blankPlate,
                clearAmulet,
                covertCloak,
                loadedDice,
                fairyFeather);
    }
}
