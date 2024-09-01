package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Move.java - represents a move usable by Pokemon.                      --*/
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

import com.dabomstew.pkrandom.constants.GlobalConstants;

public class Move {
    public static class StatChange {
        public StatChangeType type;
        public int stages;
        public double percentChance;

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != StatChange.class)
                return false;

            StatChange other = (StatChange) obj;
            return this.type == other.type && this.stages == other.stages && this.percentChance == other.percentChance;
        }

        public StatChange() {
            type = StatChangeType.NONE;
            stages = 0;
            percentChance = 0.0;
        }

        public StatChange(StatChangeType type, int stages, double percentChance) {
            this.type = type;
            this.stages = stages;
            this.percentChance = percentChance;
        }

        public StatChange(String str, int defaultChance) {
            if (str.startsWith("[") && str.endsWith("]"))
                str = str.substring(1, str.length() - 1);

            String[] components = str.split(" ");

            if (components.length == 1 && components[0].trim().isEmpty()) {
                type = StatChangeType.NONE;
                stages = 0;
                percentChance = 0;
                return;
            }

            if (components.length > 3)
                throw new RuntimeException("Incorrect length");

            switch (components[0].trim().toLowerCase()) {
                case "atk":
                    type = StatChangeType.ATTACK;
                    break;
                case "def":
                    type = StatChangeType.DEFENSE;
                    break;
                case "spa":
                    type = StatChangeType.SPECIAL_ATTACK;
                    break;
                case "spd":
                    type = StatChangeType.SPECIAL_DEFENSE;
                    break;
                case "spe":
                    type = StatChangeType.SPEED;
                    break;
                case "acc":
                    type = StatChangeType.ACCURACY;
                    break;
                case "eva":
                    type = StatChangeType.EVASION;
                    break;
                case "all":
                    type = StatChangeType.ALL;
                    break;
                default:
                    throw new RuntimeException(String.format("Unrecognized StatChange type \"%s\"", components[0]));
            }

            stages = Integer.parseInt(components[1]);

            if (components.length < 3) {
                percentChance = defaultChance;
                return;
            }

            components[2] = components[2].trim();
            if (components[2].endsWith("%"))
                components[2] = components[2].substring(0, components[2].length() - 1);

            percentChance = Integer.parseInt(components[2]);
        }
    }

    public String name;
    public String description;
    public int number = 0;
    public int internalId = 0;
    public Type type = Type.NORMAL; // 00
    public MoveQualities qualities; // 01
    public MoveCategory category = MoveCategory.STATUS; // 02
    public int power = 0; // 03
    public double accuracy = 0; // 04
    public int pp = 0; // 05
    public int priority = 0; // 06
    public int minHits = 0; // 07
    public int maxHits = 0; // 07
    public MoveStatusType statusType = MoveStatusType.NONE; // 08-09, 11
    public double statusPercentChance = 0; // 10
    public int statusMinTurns = -1; // 12
    public int statusMaxTurns = -1; // 13
    public CriticalChance criticalChance = CriticalChance.NORMAL; // 14
    public double flinchPercentChance = 0; // 15
    public MoveEffect effect = MoveEffect.DMG; // 16
    public int recoil = 0; // 18, can be regained health too
    public int heal = 0; // 19
    public MoveTarget target = MoveTarget.ANY_ADJACENT; // 20
    public StatChange[] statChanges = new StatChange[3]; // 21-29
    public boolean makesContact = false; // 30 (0x0001)
    public boolean isChargeMove = false; // 30 (0x0002)
    public boolean isRechargeMove = false; // 30 (0x0004)
    public boolean isBlockedByProtect = false; // 30 (0x0008)
    public boolean isReflectedByMagicCoat = false; // 30 (0x0010)
    public boolean isStolenBySnatch = false; // 30 (0x0020)
    public boolean isCopiedByMirrorMove = false; // 30 (0x0040)
    public boolean isPunchMove = false; // 30 (0x080)
    public boolean isSoundMove = false; // 30 (0x0100)
    public boolean isAffectedByGravity = false; // 30 (0x0200)
    public boolean isThawingMove = false; // 30 (0x0400)
    public boolean hitsNonAdjacentTargets = false; // 30 (0x0800)
    public boolean isHealMove = false; // 30 (0x1000)
    public boolean bypassesSubstitute = false; // 30 (0x2000)
    public boolean unknownFlag1 = false; // 30 (0x4000)
    public boolean unknownFlag2 = false; // 30 (0x8000)

    // Custom
    public boolean isCustomKickMove = false; // 30 (0x4000)
    public boolean isCustomBiteMove = false; // 30 (0x8000)
    public boolean isCustomSliceMove = false; // 30 (0x10000)
    public boolean isCustomTriageMove = false; // 30 (0x20000)
    public boolean isCustomPowderMove = false; // 30 (0x40000)
    public boolean isCustomWindMove = false; // 30 (0x80000)
    public boolean isCustomBallBombMove = false; // 30 (0x100000)
    public boolean isCustomPulseMove = false; // 30 (0x200000)

    public Move() {
        // Initialize all statStageChanges to something sensible so that we don't need to have
        // each RomHandler mess with them if they don't need to.
        for (int i = 0; i < this.statChanges.length; i++) {
            this.statChanges[i] = new StatChange();
            this.statChanges[i].type = StatChangeType.NONE;
        }
    }

    public boolean isTrapMove() {
        return statusType == MoveStatusType.TRAP || effect == MoveEffect.PREVENT_ESCAPE;
    }

    public StatChangeMoveType getStatChangeMoveType() {
        if (qualities == null) {
            // TODO: Finish this later for Gen IV

            return StatChangeMoveType.DAMAGE_TARGET;
        }

        switch (qualities) {
            case NO_DAMAGE_STAT_CHANGE:
            case NO_DAMAGE_STAT_CHANGE_STATUS:
                switch (target) {
                    case ADJACENT_ALLY:
                        return StatChangeMoveType.NO_DAMAGE_ALLY;
                    case PARTY:
                    case USER:
                        return StatChangeMoveType.NO_DAMAGE_USER;
                    case ALL_ON_FIELD:
                        return StatChangeMoveType.NO_DAMAGE_ALL;
                    default:
                        return StatChangeMoveType.NO_DAMAGE_TARGET;
                }
            case DAMAGE_TARGET_STAT_CHANGE:
                return StatChangeMoveType.DAMAGE_TARGET;
            case DAMAGE_USER_STAT_CHANGE:
                return StatChangeMoveType.DAMAGE_USER;
            default:
                return StatChangeMoveType.NONE_OR_UNKNOWN;
        }
    }

    public boolean hasSpecificStatChange(StatChangeType type, boolean isPositive) {
        for (StatChange sc : this.statChanges) {
            if (sc.type == type && (isPositive ^ sc.stages < 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBeneficialStatChange() {
        return (getStatChangeMoveType() == StatChangeMoveType.DAMAGE_TARGET && statChanges[0].stages < 0) ||
                getStatChangeMoveType() == StatChangeMoveType.DAMAGE_USER && statChanges[0].stages > 0;
    }

    public boolean hasPerfectAccuracy() {
        return accuracy < 1 || accuracy > 100;
    }

    public static int getPerfectAccuracy() {
        return 101;
    }

    public boolean isCounterMove() {
        return effect == MoveEffect.COUNTER || effect == MoveEffect.MIRROR_COAT || effect == MoveEffect.METAL_BURST;
    }

    public boolean isExplosionMove() {
        return effect == MoveEffect.USER_FAINTS;
    }

    public boolean isDirectDamageMove() {
        return effect == MoveEffect.DIRECT_HALF || effect == MoveEffect.DIRECT_40
                || effect == MoveEffect.DIRECT_DMG_LEVEL || effect == MoveEffect.DIRECT_DMG_20
                || effect == MoveEffect.PSYWAVE;
    }

    public boolean isOHKOMove() {
        return qualities == MoveQualities.OHKO || effect == MoveEffect.OHKO;
    }

    public boolean canBeDamagingMove(int generation) {
        if (isExplosionMove() || isDirectDamageMove())
            return false;

        if (power * getHitCount(generation) < 20)
            return false;

        switch (effect) {
            case DREAM_EATER:
            case SNORE:
            case FALSE_SWIPE:
            case FUTURE_SIGHT:
            case FAKE_OUT:
            case FOCUS_PUNCH:
            case FEINT:
            case LAST_RESORT:
            case SUCKER_PUNCH:
            case RAGE:
            case ROLLOUT:
            case SYNCHRONOISE:
                // case SHELL_TRAP; TODO
            case FOUL_PLAY:
            case SPIT_UP:
            case OHKO:
                return false;
        }

        return true;
    }

    public double getHitCount(int generation) {
        switch (effect) {
            case HIT_2_TO_5_TIMES:
                return generation < 5 ? 3.0 : 3.1;
            case HIT_2_TIMES:
            case HIT_2_TIMES_POISON:
                return 2.0;
            case TRIPLE_KICK:
                double acc = accuracy / 100.0;
                return 1 + acc + (acc * acc); // Assumes first hit lands
            default:
                return 1.0;
        }
    }

    public StatusMoveType getStatusMoveType() {
        if (qualities == null) {
            // TODO: Finish this later for Gen IV

            switch (category) {
                case PHYSICAL:
                case SPECIAL:
                    return StatusMoveType.DAMAGE;
                case STATUS:
                    return StatusMoveType.NO_DAMAGE;
                default:
                    return StatusMoveType.NONE_OR_UNKNOWN;
            }
        }

        switch (qualities) {
            case NO_DAMAGE_STATUS:
                return StatusMoveType.NO_DAMAGE;
            case DAMAGE_TARGET_STATUS:
                return StatusMoveType.DAMAGE;
            default:
                return StatusMoveType.NONE_OR_UNKNOWN;
        }
    }

    public boolean isGoodDamaging(int generation) {
        double hitCount = getHitCount(generation);
        double acc = hasPerfectAccuracy() ? 1.0 : accuracy / 100.0;
        double damage = power * hitCount * acc;
        if (effect == MoveEffect.TRIPLE_KICK)
            damage += (10 * acc) + (20 * acc * acc); // 2nd and 3rd hit additional damages

        return damage >= GlobalConstants.MIN_DAMAGING_MOVE_POWER;
    }

    public boolean isRecoilMove() {
        return effect != MoveEffect.STRUGGLE && recoil < 0;
    }

    public int getRecoilPercent() {
        return isRecoilMove() ? -recoil : 0;
    }

    public boolean isAbsorbMove() {
        return qualities == MoveQualities.DRAIN_HEALTH && recoil > 0;
    }

    public int getAbsorbPercent() {
        return isAbsorbMove() ? recoil : 0;
    }

    public String toString() {
        return String.format("#%d %s - Type: %s, Power: %d, Acc: %d%%, PP: %d, Priority: %+d, Effect: %s", number, name, type, power, Math.round(accuracy), pp, priority, effect);
    }
}
