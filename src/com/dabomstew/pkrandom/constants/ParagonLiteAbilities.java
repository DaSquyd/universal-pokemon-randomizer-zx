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
    public static final int homeGrown = 514;
    public static final int ravenousTorque = 515;
    public static final int superconductor = 516;

    public static final int MAX = ripTide;
    
    public static Set<Integer> allowedAbilities = Set.of(
            Abilities.protean, // 168
            Abilities.furCoat, // 169
            Abilities.bulletproof, // 171
            Abilities.competitive, // 172
            Abilities.strongJaw, // 173
            Abilities.refrigerate, // 174
            Abilities.galeWings, // 175 
            Abilities.megaLauncher, // 178
            Abilities.toughClaws, // 181
            Abilities.pixilate, // 182
            Abilities.gooey, // 183
            Abilities.aerilate, // 184
            Abilities.stamina, // 192
            Abilities.steelworker, // 200
            Abilities.slushRush, // 202
            Abilities.triage, // 205
            Abilities.galvanize, // 206
            Abilities.fluffy, // 218
//            Abilities.cottonDown, // 238
            Abilities.windRider, // 274
//            Abilities.windPower, // 277
            Abilities.sharpness, // 292
            Abilities.supremeOverlord, // 293
            
            heavyWing, // 500
            specialized, // 501
            insectivore, // 502
            prestige, // 503
            luckyFoot, // 504
            assimilate, // 505
            stoneHome, // 506
            cacophony // 507
//            ripTide, // 508
//            windWhipper, // 509
//            glazeware, // 510
//            sunSoaked, // 511
//            colossal, // 512
//            finalThread, // 513
//            homeGrown, // 514
//            ravenousTorque, // 515
//            superconductor, // 516
    );
}
