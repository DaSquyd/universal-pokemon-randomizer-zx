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

public class ParagonLiteAbilities {

    public static final int xrayVision = Abilities.frisk;
    public static final int herbivore = Abilities.sapSipper;

    public static final int heavyWing = 500;
    public static final int specialized = 501;
    public static final int insectivore = 502;
    public static final int prestige = 503;
    public static final int luckyFoot = 504;
    public static final int assimilate = 505;
    public static final int stoneHome = 506;
    public static final int cacophony = 507;
    public static final int ripTide = 508;
    public static final int windWhipper = 509;
    public static final int glazeware = 510;
    public static final int sunSoaked = 511;
    public static final int colossal = 512;
    public static final int finalThread = 513;
    public static final int homegrown = 514;
    public static final int ravenousTorque = 515;
    public static final int superconductor = 516;
    public static final int somaticReflex = 517;
    public static final int incendiate = 518;
    public static final int liquidate = 519;
    public static final int florilate = 520;
    public static final int contaminate = 521;
    public static final int volcanicFury = 522;
    public static final int pastoralAroma = 523;
    public static final int healSpore = 524;

    public static final int MAX = healSpore;
    
    public static Set<Integer> allowedAbilities = Set.of(
//            Abilities.aromaVeil, // 165
//            Abilities.flowerVeil, // 166
//            Abilities.cheekPouch, // 167
            Abilities.protean, // 168
            Abilities.furCoat, // 169
//            Abilities.magician, // 170
            Abilities.bulletproof, // 171
            Abilities.competitive, // 172
            Abilities.strongJaw, // 173
            Abilities.refrigerate, // 174
//            Abilities.sweetVeil, // 175
            
            Abilities.galeWings, // 177
            Abilities.megaLauncher, // 178
//            Abilities.grassPelt, // 179
//            Abilities.symbiosis, // 180
            Abilities.toughClaws, // 181
            Abilities.pixilate, // 182
            Abilities.gooey, // 183
            Abilities.aerilate, // 184
//            Abilities.parentalBond, // 185
//            Abilities.darkAura, // 186
//            Abilities.fairyAura, // 187
//            Abilities.auraBreak, // 188
            
            Abilities.stamina, // 192
//            Abilities.wimpOut, // 193
//            Abilities.emergencyExit, // 194
//            Abilities.waterCompaction, // 195
//            Abilities.merciless, // 196
//            Abilities.shieldsDown, // 197
//            Abilities.stakeout, // 198
//            Abilities.waterBubble, // 199
            Abilities.steelworker, // 200
//            Abilities.berserk, // 201
            Abilities.slushRush, // 202
//            Abilities.longReach, // 203
            Abilities.liquidVoice, // 204
            Abilities.triage, // 205
            Abilities.galvanize, // 206
//            Abilities.surgeSurfer, // 207
            
//            Abilities.corrosion, // 212
//            Abilities.comatose, // 213
//            Abilities.queenlyMajesty, // 214
//            Abilities.innardsOut, // 215
//            Abilities.dancer, // 216
//            Abilities.battery, // 217
            Abilities.fluffy, // 218
//            Abilities.dazzling, // 219
//            Abilities.soulHeart, // 220
//            Abilities.tanglingHair, // 221
//            Abilities.receiver, // 222
//            Abilities.powerOfAlchemy, // 223
//            Abilities.beastBoost, // 224
            
//            Abilities.electricSurge, // 226
//            Abilities.psychicSurge, // 227
//            Abilities.mistySurge, // 228
//            Abilities.grassySurge, // 229
//            Abilities.fullMetalBody, // 230
//            Abilities.shadowShield, // 231
//            Abilities.prismArmor, // 232
//            Abilities.neuroforce, // 233
//            Abilities.intrepidSword, // 234
//            Abilities.dauntlessShield, // 235
//            Abilities.libero, // 236
            
//            Abilities.cottonDown, // 238
//            Abilities.quickDraw, // 259
            Abilities.transistor, // 262
            Abilities.dragonsMaw, // 263
//            Abilities.thermalExchange, // 270
            Abilities.windRider, // 274
            Abilities.rockyPayload, // 276
//            Abilities.windPower, // 277
//            Abilities.goodAsGold, // 283
//            Abilities.cudChew, // 291
            Abilities.sharpness, // 292
            Abilities.supremeOverlord, // 293
//            Abilities.toxicDebris, // 295
            
            heavyWing, // 500
            specialized, // 501
            insectivore, // 502
            prestige, // 503
            luckyFoot, // 504
            assimilate, // 505
            stoneHome, // 506
            cacophony, // 507
//            ripTide, // 508
            windWhipper, // 509
            glazeware, // 510
            sunSoaked, // 511
            colossal, // 512
//            finalThread, // 513
//            homeGrown, // 514
//            ravenousTorque, // 515
//            superconductor, // 516
//            somaticReflex, // 517
            incendiate, // 518
            liquidate, // 519
            florilate, // 520
            contaminate, // 521
//            volcanicFury, // 522
//            pastoralAroma, // 523
            healSpore // 524
    );
}
