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
import com.dabomstew.pkrandom.constants.Moves;

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

        public StatChange(StatChangeType type, int stages) {
            this.type = type;
            this.stages = stages;
            this.percentChance = 0.0;
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
    public String shopDescription;
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
    public boolean makesContact = false; // 30 (0x00000001)
    public boolean isChargeMove = false; // 30 (0x00000002)
    public boolean isRechargeMove = false; // 30 (0x00000004)
    public boolean isBlockedByProtect = false; // 30 (0x00000008)
    public boolean isReflectedByMagicCoat = false; // 30 (0x00000010)
    public boolean isStolenBySnatch = false; // 30 (0x00000020)
    public boolean isCopiedByMirrorMove = false; // 30 (0x00000040)
    public boolean isPunchMove = false; // 30 (0x0000080)
    public boolean isSoundMove = false; // 30 (0x00000100)
    public boolean isAffectedByGravity = false; // 30 (0x00000200)
    public boolean isThawingMove = false; // 30 (0x00000400)
    public boolean hitsNonAdjacentTargets = false; // 30 (0x00000800)
    public boolean isHealMove = false; // 30 (0x00001000)
    public boolean bypassesSubstitute = false; // 30 (0x00002000)
    public boolean unknownFlag1 = false; // 30 (0x00004000)
    public boolean unknownFlag2 = false; // 30 (0x00008000)

    // Custom
    public boolean isCustomKickMove = false; // 30 (0x00004000)
    public boolean isCustomBiteMove = false; // 30 (0x00008000)
    public boolean isCustomSliceMove = false; // 30 (0x00010000)
    public boolean isCustomTriageMove = false; // 30 (0x00020000)
    public boolean isCustomPowderMove = false; // 30 (0x00040000)
    public boolean isCustomWindMove = false; // 30 (0x00080000)
    public boolean isCustomBallBombMove = false; // 30 (0x00100000)
    public boolean isCustomPulseMove = false; // 30 (0x00200000)
    public boolean isCustomDanceMove = false; // 30 (0x00400000)
    public boolean isCustomRollSpinMove = false; // 30 (0x00800000)
    public boolean isCustomLightMove = false; // 30 (0x01000000)
    public boolean isCustomBeamMove = false; // 30 (0x02000000)

    public Move() {
        // Initialize all statStageChanges to something sensible so that we don't need to have
        // each RomHandler mess with them if they don't need to.
        for (int i = 0; i < this.statChanges.length; i++) {
            this.statChanges[i] = new StatChange();
            this.statChanges[i].type = StatChangeType.NONE;
        }
    }

    public String getDisplayName() {
        if (name.length() > 15)
            return name.replace(" ", "");
        return name;
    }
    
    public String getSafeShopDescription() {
        return shopDescription.isEmpty() ? description : shopDescription;
    }

    public boolean isTrapMove() {
        return statusType == MoveStatusType.TRAP
                || effect == MoveEffect.DMG_TRAP
                || effect == MoveEffect.PREVENT_ESCAPE;
    }

    public boolean hasSpecificStatChange(StatChangeType type, boolean isPositive) {
        for (StatChange sc : this.statChanges) {
            if (sc.type == type && (isPositive ^ sc.stages < 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFoeTargeted() {
        switch (target) {
            case ANY_ADJACENT, ADJACENT_FOE, ALL_ADJACENT, ALL_ADJACENT_FOES, ALL_ON_FIELD, RANDOM_ADJACENT_FOE -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean hasBeneficialStatChange() {
        switch (target) {
            case ANY_ADJACENT, ADJACENT_FOE, ALL_ADJACENT, ALL_ADJACENT_FOES, ALL_ON_FIELD, RANDOM_ADJACENT_FOE -> {
                return statChanges[0].stages < 0;
            }
            case USER_OR_ALLY, ADJACENT_ALLY, PARTY, USER -> {
                return statChanges[0].stages > 0;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean hasDetrimentalStatChange() {
        switch (target) {
            case ANY_ADJACENT, ADJACENT_FOE, ALL_ADJACENT, ALL_ADJACENT_FOES, ALL_ON_FIELD, RANDOM_ADJACENT_FOE -> {
                return statChanges[0].stages > 0;
            }
            case USER_OR_ALLY, ADJACENT_ALLY, PARTY, USER -> {
                return statChanges[0].stages < 0;
            }
            default -> {
                return false;
            }
        }
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
        return effect == MoveEffect.EXPLOSION;
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

        return switch (effect) {
            // case SHELL_TRAP; TODO
            case DREAM_EATER, SNORE, FALSE_SWIPE, FUTURE_SIGHT, FAKE_OUT, FOCUS_PUNCH, FEINT, LAST_RESORT, SUCKER_PUNCH, RAGE, ROLLOUT, SYNCHRONOISE, FOUL_PLAY, SPIT_UP, OHKO ->
                    false;
            default -> true;
        };
    }

    public double getHitCount(int generation) {
        double acc = accuracy / 100.0;
        return switch (effect) {
            case HIT_2_TO_5_TIMES -> generation < 5 ? 3.0 : 3.1;
            case HIT_2_TIMES, HIT_2_TIMES_POISON -> 1 + acc; // TODO: This is dependent on ParagonLite settings
            case TRIPLE_KICK -> 1 + acc + (acc * acc); // Assumes first hit lands
            default -> 1.0;
        };
    }

    public boolean isDamagingMove() {
        if (power > 0)
            return true;

        if (category != null)
            return switch (category) {
                case PHYSICAL, SPECIAL -> true;
                case STATUS -> false;
            };

        if (qualities != null)
            return switch (qualities) {
                case DAMAGE, DAMAGE_TARGET_STATUS, DAMAGE_TARGET_STAT_CHANGE, DAMAGE_USER_STAT_CHANGE, DRAIN_HEALTH, OHKO -> true;
                case NO_DAMAGE_STATUS, NO_DAMAGE_STAT_CHANGE, HEAL, NO_DAMAGE_STAT_CHANGE_STATUS, FIELD_EFFECT, TEAM_EFFECT, FORCE_SWITCH, OTHER -> false;
            };

        if (effect.qualities != null)
            return switch (effect.qualities) {
                case DAMAGE, DAMAGE_TARGET_STATUS, DAMAGE_TARGET_STAT_CHANGE, DAMAGE_USER_STAT_CHANGE, DRAIN_HEALTH, OHKO -> true;
                case NO_DAMAGE_STATUS, NO_DAMAGE_STAT_CHANGE, HEAL, NO_DAMAGE_STAT_CHANGE_STATUS, FIELD_EFFECT, TEAM_EFFECT, FORCE_SWITCH, OTHER -> false;
            };

        return false;
    }

    public boolean isGoodDamaging(int generation) {
        if (recoil > 0 || effect == MoveEffect.JUMP_KICK || effect == MoveEffect.FLY || effect == MoveEffect.THRASH_ABOUT || isChargeMove || isRechargeMove)
            return false;

        double hitCount = getHitCount(generation);
        double acc = hasPerfectAccuracy() ? 1.0 : accuracy / 100.0;
        double damage = power * hitCount * acc;
        // TODO
//        if (effect == MoveEffect.TRIPLE_KICK)
//            damage += (10 * acc) + (20 * acc * acc); // 2nd and 3rd hit additional damages

        return damage >= GlobalConstants.MIN_DAMAGING_MOVE_POWER;
    }

    public boolean isRecoilMove() {
        return effect != MoveEffect.STRUGGLE && recoil < 0;
    }

    public int getRecoilPercent() {
        return isRecoilMove() ? -recoil : 0;
    }

    public boolean isBoostedBySheerForce() {
        if (qualities == MoveQualities.DAMAGE_TARGET_STATUS || qualities == MoveQualities.DAMAGE_TARGET_STAT_CHANGE || (qualities == MoveQualities.DAMAGE_USER_STAT_CHANGE && statChanges[0].stages > 0) || flinchPercentChance > 0)
            return true;

        return switch (number) {
            case Moves.secretPower, Moves.spiritShackle, Moves.sparklingAria, Moves.anchorShot, Moves.genesisSupernova, Moves.eerieSpell, Moves.stoneAxe,
                 Moves.ceaselessEdge, Moves.orderUp, Moves.electroShot -> true;
            default -> false;
        };
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

    public boolean makesObsolete(Move other, boolean doubleBattleMode) {
        if (this == other)
            return false;

        switch (qualities) {
            case DAMAGE -> {
                if (other.qualities != MoveQualities.DAMAGE || effect != other.effect)
                    return false;
            }
            case NO_DAMAGE_STATUS, NO_DAMAGE_STAT_CHANGE, NO_DAMAGE_STAT_CHANGE_STATUS, OHKO, FIELD_EFFECT, TEAM_EFFECT, FORCE_SWITCH -> {
                if (qualities != other.qualities)
                    return false;
            }
            case DAMAGE_TARGET_STATUS, DAMAGE_TARGET_STAT_CHANGE, DAMAGE_USER_STAT_CHANGE, DRAIN_HEALTH -> {
                if (other.qualities != MoveQualities.DAMAGE && qualities != other.qualities)
                    return false;

                if (other.qualities == MoveQualities.DAMAGE && effect != other.effect)
                    return false;
            }
            case HEAL -> {
                if (qualities != other.qualities || effect != other.effect)
                    return false;
            }
            case OTHER -> {
                return false;
            }
        }

        // Incomparable
        if (type != other.type)
            return false;

        // Incomparable
        if (category != other.category)
            return false;

        // Incomparable
        if ((power > 1) != (other.power > 1))
            return false;

        // Incomparable
        if (minHits != other.minHits || maxHits != other.maxHits)
            return false;

        // Incomparable
        if (statusType != other.statusType)
            return false;

        // Incomparable
        if (isTrapMove() != other.isTrapMove())
            return false;

        // Incomparable
        if (isOHKOMove() != other.isOHKOMove())
            return false;

        // Incomparable
        if (isChargeMove != other.isChargeMove)
            return false;

        // Incomparable
        if (isRechargeMove != other.isRechargeMove)
            return false;

        double tempPower = power;
        double otherTempPower = other.power;

        if (doubleBattleMode) {
            tempPower *= target == MoveTarget.ALL_ADJACENT_FOES || target == MoveTarget.ALL_ADJACENT || target == MoveTarget.ALL_ON_FIELD ? 0.75 : 1.0;
            otherTempPower *= other.target == MoveTarget.ALL_ADJACENT_FOES || other.target == MoveTarget.ALL_ADJACENT || other.target == MoveTarget.ALL_ON_FIELD ? 0.75 : 1.0;

            switch (other.target) {
                // Other is single-foe target
                case ANY_ADJACENT, ADJACENT_FOE -> {
                    if (target == MoveTarget.ALL_ADJACENT_FOES) {
                        if (tempPower < otherTempPower)
                            return false;
                    }
                }
                default -> {
                    if (target != other.target)
                        return false;

                    if (tempPower < otherTempPower)
                        return false;
                }
            }
        } else if (isFoeTargeted() != other.isFoeTargeted()) {
            return false;
        } else if (tempPower < otherTempPower) {
            return false;
        }

        // Worse
        if (accuracy < other.accuracy)
            return false;

        // Worse
        if (priority < other.priority)
            return false;

        // Worse
        if (statusPercentChance < other.statusPercentChance)
            return false;

        // Worse
        if (criticalChance.ordinal() < other.criticalChance.ordinal())
            return false;

        // Worse
        if (flinchPercentChance < other.flinchPercentChance)
            return false;

        if (hasDetrimentalStatChange() && !other.hasDetrimentalStatChange())
            return false;

        if (!hasBeneficialStatChange() && other.hasBeneficialStatChange())
            return false;

        // Worse (other has stat boost that we don't or better for what we do have
        for (StatChange otherStatChange : other.statChanges) {
            if (otherStatChange.type == StatChangeType.NONE)
                continue;

            boolean has = false;
            for (StatChange statChange : statChanges) {
                if (statChange.type == StatChangeType.NONE || statChange.type != otherStatChange.type)
                    continue;

                has = true;

                if (Math.abs(statChange.stages) < Math.abs(otherStatChange.stages))
                    return false;

                if (statChange.percentChance < otherStatChange.percentChance)
                    return false;
            }

            if (!has)
                return false;
        }

        // Main success condition
        if (tempPower > otherTempPower
                || accuracy > other.accuracy
                || priority > other.priority
                || statusPercentChance > other.statusPercentChance
                || criticalChance.ordinal() > other.criticalChance.ordinal()
                || flinchPercentChance > other.flinchPercentChance)
            return true;

        for (StatChange statChange : statChanges) {
            if (statChange.type == StatChangeType.NONE)
                continue;

            boolean has = false;
            for (StatChange otherStatChange : other.statChanges) {
                if (statChange.type != otherStatChange.type)
                    continue;

                has = true;

                if (Math.abs(statChange.stages) > Math.abs(otherStatChange.stages))
                    return true;

                if (statChange.percentChance > otherStatChange.percentChance)
                    return true;
            }

            if (!has)
                return true;
        }

        // Effectively the same move
        return false;
    }
}
