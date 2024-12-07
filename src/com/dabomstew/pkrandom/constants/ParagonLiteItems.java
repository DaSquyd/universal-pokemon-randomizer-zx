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
    public static final int rustyBottleCap = Items.MAX + 1;
    
    public static final int MAX = rustyBottleCap;
    
    public static Set<Integer> getAllowed() {
        return Set.of(
                Items.weaknessPolicy, // 639
                Items.assaultVest, // 640
                Items.pixiePlate, // 644
//                Items.snowball, // 849
//                Items.safetyGoggles, // 650
                Items.roseliBerry, // 686
//                Items.keeBerry, // 687
//                Items.marangaBerry, // 688
                Items.fairyGem, // 715
//                Items.bottleCap, // 795
//                Items.goldBottleCap, // 796
//                Items.adrenalineOrb, // 846
//                Items.iceStone, // 849
//                Items.terrainExtender, // 879
//                Items.protectivePads, // 880
//                Items.electricSeed, // 881
//                Items.psychicSeed, // 882
//                Items.mistySeed, // 883
//                Items.grassySeed, // 884
//                Items.throatSpray, // 1118
//                Items.ejectPack, // 1119
//                Items.heavyDutyBoots, // 1120
//                Items.blunderPolicy, // 1121
//                Items.roomService, // 1122
//                Items.utilityUmbrella, // 1123
//                Items.lonelyMint, // 1231
//                Items.adamantMint, // 1232
//                Items.naughtyMint, // 1233
//                Items.braveMint, // 1234
//                Items.boldMint, // 1235
//                Items.impishMint, // 1236
//                Items.laxMint, // 1237
//                Items.relaxedMint, // 1238
//                Items.modestMint, // 1239
//                Items.mildMint, //1240
//                Items.rashMint, // 1241
//                Items.quietMint, // 1242
//                Items.calmMint, // 1243
//                Items.gentleMint, // 1244
//                Items.carefulMint, // 1245
//                Items.sassyMint, // 1246
//                Items.timidMint, // 1247
//                Items.hastyMint, // 1248
//                Items.jollyMint, // 1249
//                Items.naiveMint, // 1250
//                Items.seriousMint, // 1251
                Items.linkingCord, // 1611
                Items.blankPlate, // 1780
//                Items.legendPlate, // 1828
//                Items.abilityShield, // 1881
                Items.clearAmulet, // 1882
//                Items.mirrorHerb, // 1883
//                Items.punchingGlove, // 1884
                Items.covertCloak, // 1885
                Items.loadedDice, // 1886
                Items.fairyFeather, // 2401
                
                ParagonLiteItems.rustyBottleCap
        );
    }
}
