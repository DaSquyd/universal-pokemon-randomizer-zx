package com.dabomstew.pkrandom;

/*----------------------------------------------------------------------------*/
/*--  MiscTweak.java - represents a miscellaneous tweak that can be applied --*/
/*--                   to some or all games that the randomizer supports.   --*/
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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MiscTweak implements Comparable<MiscTweak> {

    public static final long NO_MISC_TWEAKS = 0L;

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");

    public static List<MiscTweak> allTweaks = new ArrayList<>();

    /* @formatter:off */
    // Higher priority value (third argument) = run first
    public static final MiscTweak BW_EXP_PATCH = new MiscTweak(1, "bwPatch", 0);
    public static final MiscTweak NERF_X_ACCURACY = new MiscTweak(1 << 1, "nerfXAcc", 0);
    public static final MiscTweak FIX_CRIT_RATE = new MiscTweak(1 << 2, "critRateFix", 0);
    public static final MiscTweak FASTEST_TEXT = new MiscTweak(1 << 3, "fastestText", 0);
    public static final MiscTweak RUNNING_SHOES_INDOORS = new MiscTweak(1 << 4, "runningShoes", 0);
    public static final MiscTweak RANDOMIZE_PC_POTION = new MiscTweak(1 << 5, "pcPotion", 0);
    public static final MiscTweak ALLOW_PIKACHU_EVOLUTION = new MiscTweak(1 << 6, "pikachuEvo", 0);
    public static final MiscTweak NATIONAL_DEX_AT_START = new MiscTweak(1 << 7, "nationalDex", 0);
    public static final MiscTweak UPDATE_TYPE_EFFECTIVENESS = new MiscTweak(1 << 8, "typeEffectiveness", 0);
    public static final MiscTweak FORCE_CHALLENGE_MODE = new MiscTweak(1 << 9, "forceChallengeMode", 0);
    public static final MiscTweak LOWER_CASE_POKEMON_NAMES = new MiscTweak(1 << 10, "lowerCaseNames", 0);
    public static final MiscTweak RANDOMIZE_CATCHING_TUTORIAL = new MiscTweak(1 << 11, "catchingTutorial", 0);
    public static final MiscTweak BAN_LUCKY_EGG = new MiscTweak(1 << 12, "luckyEgg", 1);
    public static final MiscTweak NO_FREE_LUCKY_EGG = new MiscTweak(1 << 13,"freeLuckyEgg",0);
    public static final MiscTweak BAN_BIG_MANIAC_ITEMS = new MiscTweak(1 << 14, "maniacItems",1);
    public static final MiscTweak SOS_BATTLES_FOR_ALL = new MiscTweak(1 << 15, "sosBattles",0);
    public static final MiscTweak BALANCE_STATIC_LEVELS = new MiscTweak(1 << 16, "balanceStaticLevels",0);
    public static final MiscTweak RETAIN_ALT_FORMES = new MiscTweak(1 << 17, "retainAltFormes",0);
    public static final MiscTweak RUN_WITHOUT_RUNNING_SHOES = new MiscTweak(1 << 18, "runWithoutRunningShoes", 0);
    public static final MiscTweak FASTER_HP_AND_EXP_BARS = new MiscTweak(1 << 19, "fasterHpAndExpBars", 0);
    public static final MiscTweak FAST_DISTORTION_WORLD = new MiscTweak(1 << 20, "fastDistortionWorld", 0);
    public static final MiscTweak UPDATE_ROTOM_FORME_TYPING = new MiscTweak(1 << 21, "updateRotomFormeTyping", 0);
    public static final MiscTweak DISABLE_LOW_HP_MUSIC = new MiscTweak(1 << 22, "disableLowHpMusic", 0);
    public static final MiscTweak CUSTOM_POKEMON_TYPES = new MiscTweak(1 << 23, "customPokemonTypes", 0);
    public static final MiscTweak CUSTOM_TYPE_EFFECTIVENESS = new MiscTweak(1 << 24, "customTypeEffectiveness", 0);
    public static final MiscTweak CUSTOM_POKEMON_STATS = new MiscTweak(1 << 25, "customPokemonStats", 0);
    public static final MiscTweak CUSTOM_MOVE_CHANGES = new MiscTweak(1 << 26, "customMoveChanges", 0);
    public static final MiscTweak CUSTOM_NO_EXP = new MiscTweak(1 << 27, "customNoExp", 0);
    public static final MiscTweak CUSTOM_MAX_HAPPINESS = new MiscTweak(1 << 28, "customMaxHappiness", 0);
    public static final MiscTweak CUSTOM_NO_EVS = new MiscTweak(1 << 29, "customNoEVs", 0);
    public static final MiscTweak MODERNIZE_CRIT = new MiscTweak(1 << 30, "modernizeCrit", 0);
    public static final MiscTweak MODERNIZE_GEMS = new MiscTweak(1L << 31, "modernizeGems", 0);
    public static final MiscTweak NPC_SMART_AI = new MiscTweak(1L << 32, "npcSmartAI", 0);
    public static final MiscTweak CUSTOM_ADD_FAIRY = new MiscTweak(1L << 33, "customAddFairy", 1);
    public static final MiscTweak LITTLE_CUP_MODE = new MiscTweak(1L << 34, "littleCupMode", 0);
    public static final MiscTweak DEBUG_MODE = new MiscTweak(1L << 62, "debugMode", 10);
    public static final MiscTweak PARAGON_LITE = new MiscTweak(1L << 63, "paragonLite", 10);
    /* @formatter:on */

    private final long value;
    private final String tweakName;
    private final String tooltipText;
    private final int priority;

    private MiscTweak(long value, String tweakID, int priority) {
        this.value = value;
        this.tweakName = bundle.getString("CodeTweaks." + tweakID + ".name"); // this feels really dumb
        this.tooltipText = bundle.getString("CodeTweaks." + tweakID + ".toolTipText");
        this.priority = priority;
        allTweaks.add(this);
    }

    public long getValue() {
        return value;
    }

    public String getTweakName() {
        return tweakName;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    @Override
    public int compareTo(MiscTweak o) {
        // Order according to reverse priority, so higher priority = earlier in
        // ordering
        return o.priority - priority;
    }

}
