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

            StatChange other = (StatChange)obj;
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
    }

    public String name;
    public String description;
    public int number;
    public int internalId;
    public Type type; // 00
    public MoveQualities qualities; // 01
    public MoveCategory category; // 02
    public int power; // 03
    public double accuracy; // 04
    public int pp; // 05
    public int priority; // 06
    public int minHits; // 07
    public int maxHits; // 07
    public MoveStatusType statusType = MoveStatusType.NONE; // 08-09, 11-13
    public double statusPercentChance; // 10
    public CriticalChance criticalChance = CriticalChance.NORMAL; // 14
    public double flinchPercentChance; // 15
    public MoveEffect effect; // 16
    public int recoil; // 18, can be regained health too
    public int heal;
    public MoveTarget target; // 20
    public StatChange[] statChanges = new StatChange[3]; // 21-29
    public boolean makesContact; // 30 (0x0001)
    public boolean isChargeMove; // 30 (0x0002)
    public boolean isRechargeMove; // 30 (0x0004)
    public boolean isBlockedByProtect; // 30 (0x0008)
    public boolean isReflectedByMagicCoat; // 30 (0x0010)
    public boolean isStolenBySnatch; // 30 (0x0020)
    public boolean isCopiedByMirrorMove; // 30 (0x0040)
    public boolean isPunchMove; // 30 (0x080)
    public boolean isSoundMove; // 30 (0x0100)
    public boolean isAffectedByGravity; // 30 (0x0200)
    public boolean isThawingMove; // 30 (0x0400)
    public boolean hitsNonAdjacentTargets; // 30 (0x0800)
    public boolean isHealMove; // 30 (0x1000)
    public boolean hitsThroughSubstitute; // 30 (0x2000)

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
        for (StatChange sc: this.statChanges) {
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
        return !(accuracy > 1 && accuracy <= 100);
    }

    public static int getPerfectAccuracy(MoveCategory category, int generation) {
        switch (generation) {
            case 5:
            default:
                return category == MoveCategory.STATUS ? 101 : 0;
        }
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
                return 2.71; // Assumes first hit lands
            default:
                return 1.0;
        }
    }

    public StatusMoveType getStatusMoveType() {
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
        return (power * hitCount) >= 2 * GlobalConstants.MIN_DAMAGING_MOVE_POWER
                || ((power * hitCount) >= GlobalConstants.MIN_DAMAGING_MOVE_POWER && (accuracy >= 90 || hasPerfectAccuracy()));
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
        return "#" + number + " " + name + " - Power: " + power + ", Base PP: " + pp + ", Type: " + type + ", Hit%: "
                + (accuracy) + ", Effect: " + effect.toString() + ", Priority: " + priority;
    }
}
