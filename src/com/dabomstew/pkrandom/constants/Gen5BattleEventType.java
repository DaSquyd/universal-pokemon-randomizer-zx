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

public class Gen5BattleEventType {
    public static final int none = 0x00;
    public static final int onActionProcessingStart = 0x01;
    public static final int onActionProcessingEnd = 0x02;
    public static final int onMoveSequenceStart = 0x03;
    public static final int onMoveSequenceEnd = 0x04;
    public static final int onCheckBypassSubstitute = 0x05;
    public static final int onCheckDelayedMove = 0x06;
    public static final int onDecideDelayedMove = 0x07;
    public static final int onMoveSequenceSteal = 0x08;
    public static final int onMoveSequenceReflect = 0x09;
    public static final int unused0A = 0x0A;
    public static final int onSkipRunCalc = 0x0B;
    public static final int onPreventRun = 0x0C;
    public static final int onRunExitMessage = 0x0D;
    public static final int onCheckSleep = 0x0E;
    public static final int onCheckSpecialPriority = 0x0F;
    public static final int unused10 = 0x10;
    public static final int onGetMovePriority = 0x11;
    public static final int onCheckFloating = 0x12;
    public static final int onCalcSpeed = 0x13;
    public static final int unused14 = 0x14;
    public static final int onPreAttacks = 0x15;
    public static final int unused16 = 0x16;
    public static final int Unused17 = 0x17;
    public static final int onMoveRequestParam = 0x18;
    public static final int onMoveRequestMessage = 0x19;
    public static final int onCheckMoveSteal = 0x1A;
    public static final int onFloatingImmuneToMove = 0x1B;
    public static final int onSkipAvoidCheck = 0x1C;
    public static final int onCheckMoveFail = 0x1D;
    public static final int onMoveExecuteCheck1 = 0x1E;
    public static final int onMoveExecuteCheck2 = 0x1F;
    public static final int unused20 = 0x20;
    public static final int onMoveExecuteFail = 0x21;
    public static final int onChooseMove = 0x22;
    public static final int onBreakOpponentGuard = 0x23;
    public static final int onMoveExecuteStart = 0x24;
    public static final int onMoveExecuteEffective = 0x25;
    public static final int onMoveExecuteNoEffect = 0x26;
    public static final int OnMoveExecuteEnd = 0x27;
    public static final int onGetMoveParam = 0x28;
    public static final int onGetMoveTarget = 0x29;
    public static final int onRedirectTarget = 0x2A;
    public static final int onCheckNoEffect1 = 0x2B;
    public static final int onCheckNoEffect2 = 0x2C;
    public static final int onCheckNoEffect3 = 0x2D;
    public static final int onCheckProtectBreak = 0x2E;
    public static final int onAvoidMove = 0x2F;
    public static final int onCheckDamageToRecover = 0x30;
    public static final int onApplyDamageToRecover = 0x31;
    public static final int onBypassAccuracyCheck = 0x32;
    public static final int onGetMoveAccuracyStage = 0x33;
    public static final int onGetMoveAccuracy = 0x34;
    public static final int onGetHitCount = 0x35;
    public static final int onGetIsCriticalHit = 0x36;
    public static final int onGetMoveBasePower = 0x37;
    public static final int onGetMovePower = 0x38;
    public static final int onGetAttackingStat = 0x39;
    public static final int onGetDefendingStat = 0x3A;
    public static final int onGetAttackingStatValue = 0x3B;
    public static final int onGetDefendingStatValue = 0x3C;
    public static final int onGetEffectivenessEnabled = 0x3D;
    public static final int onGetEffectiveness = 0x3E;
    public static final int onRewriteEffectiveness = 0x3F;
    public static final int onGetIsSTAB = 0x40;
    public static final int onApplySTAB = 0x41;
    public static final int onGetAttackerType = 0x42;
    public static final int onGetDefenderType = 0x43;
    public static final int onPostDamageReaction = 0x44;
    public static final int onGetMoveDamage = 0x45;
    public static final int onMoveDamageProcessing1 = 0x46;
    public static final int onMoveDamageProcessing2 = 0x47;
    public static final int onMoveDamageProcessingEnd = 0x48;
    public static final int onMoveDamageProcessingFinal = 0x49;
    public static final int onPreviousMoveReaction = 0x4A; // Shield Dust
    public static final int onMoveDamageReaction1 = 0x4B;
    public static final int onMoveDamageReaction2 = 0x4C; // Destiny Bond
    public static final int onPostMoveDamageSide = 0x4D;
    public static final int onDecrementPP = 0x4E;
    public static final int onDecrementPPDone = 0x4F;
    public static final int onCalcRecoil = 0x50;
    public static final int onAddStatStageChangeTarget = 0x51;
    public static final int onAddStatStageChangeUser = 0x52;
    public static final int onSwitchOutInterrupt = 0x53;
    public static final int onSwitchOutEnd = 0x54;
    public static final int onSwitchIn = 0x55;
    public static final int onSwitchInPrevious = 0x56;
    public static final int onPostLastSwitchIn = 0x57;
    public static final int onRotateIn = 0x58;
    public static final int onGetStatStageChangeValue = 0x59;
    public static final int onStatStageChange = 0x5A;
    public static final int onStatStageChangeLastCheck = 0x5B;
    public static final int onStatStageChangeFail = 0x5C;
    public static final int onStatStageChangeSuccess = 0x5D;
    public static final int onMoveStatStageChangeApplied = 0x5E;
    public static final int onMoveConditionTurnCount = 0x5F;
    public static final int onMoveConditionSpecial = 0x60;
    public static final int onMoveConditionMessage = 0x61;
    public static final int onMoveConditionParam = 0x62;
    public static final int onAddConditionType = 0x63;
    public static final int onAddCondition = 0x64;
    public static final int onAddConditionCheckFail = 0x65;
    public static final int onAddConditionSuccess = 0x66;
    public static final int onAddConditionFail = 0x67;
    public static final int onAddBasicStatus = 0x68;
    public static final int onAddMoveConditionSuccess = 0x69;
    public static final int onAbilityNullified = 0x6A;
    public static final int onConditionDamage = 0x6B;
    public static final int onGetMoveFlinchChance = 0x6C;
    public static final int onFlinchCheck = 0x6D;
    public static final int onFlinchFail = 0x6E;
    public static final int onFlinchSuccess = 0x6F;
    public static final int onOHKOCheck = 0x70;
    public static final int onOHKOPrevent = 0x71;
    public static final int onUseItem = 0x72;
    public static final int onUseItemTemp = 0x73;
    public static final int onEndureCheck = 0x74;
    public static final int onEndure = 0x75;
    public static final int onTurnCheckBegin = 0x76;
    public static final int onTurnCheckEnd = 0x77;
    public static final int onTurnCheckDone = 0x78;
    public static final int onNotifyAirLock = 0x79;
    public static final int onWeatherCheck = 0x7A;
    public static final int onGetWeight = 0x7B;
    public static final int onMoveWeatherTurnCount = 0x7C;
    public static final int onWeatherChange = 0x7D;
    public static final int onPostWeatherChange = 0x7E;
    public static final int onWeatherReaction = 0x7F;
    public static final int onEnableSimpleDamage = 0x80;
    public static final int onDamageProcessingStart = 0x81;
    public static final int onDamageProcessingEnd_PreviousHit = 0x82; // Sheer Force, Shield Dust

    /// The following 5 are canceled if Sheer Force takes effect (only applicable for a few here)
    
    // onHit:                   Flame Burst
    // onAfterSubDamage:        Flame Burst
    // onAfterMoveSecondary:    Color Change, Pickpocket
    
    /// "onHit" in Showdown, happens between Hit1 and Hit2
    public static final int onDamageProcessingEnd_HitReal = 0x83;
    // Items:       Red Card, Eject Button
    // Abilities:   Color Change, Moxie
    // Moves:       Secret Power, Wake-Up Slap, Smelling Salts, Knock Off, Thief/Covet, Pluck/Bug Bite, Circle Throw/Dragon Tail, Smack Down
    
    /// "onTryMove" in Showdown
    public static final int onDamageProcessingEnd_Hit1 = 0x84;
    // Moves:       Shadow Force

    /// "onHit" in Showdown
    public static final int onDamageProcessingEnd_Hit2 = 0x85;
    // Items:       Life Orb, Shell Bell
    // Moves:       Flame Burst, Relic Song, Water Pledge/Fire Pledge/Grass Pledge

    /// handled as part of "selfSwitch" in Showdown, happens after onCheckItemReaction
    public static final int onDamageProcessingEnd_Hit3 = 0x86;
    // Moves:       U-turn/Volt Switch
    
    /// "onAfterMoveSecondary" in Showdown
    public static final int onDamageProcessingEnd_Hit4 = 0x87;
    // Abilities:   Pickpocket
    
    public static final int onDamageProcessingEnd = 0x88;
    public static final int onPreAbilityChange = 0x89;
    public static final int onPostAbilityChange = 0x8A;
    public static final int onCheckForceSwitch = 0x8B;
    public static final int onCalcDrain = 0x8C;
    public static final int onCalcDrainEnd = 0x8D;
    public static final int unknown8E = 0x8E;
    public static final int onRecoverHealth = 0x8F;
    public static final int onPostItemEquip = 0x90;
    
    /// "onUpdate" in Showdown
    public static final int onCheckItemReaction = 0x91;
    // Items:       Pretty much all Berries that do something
    
    public static final int onItemConsumed = 0x92;
    public static final int onCheckChargeUpFail = 0x93;
    public static final int onCheckChargeUpSkip = 0x94;
    public static final int onChargeUpStart = 0x95;
    public static final int onChargeUpStartDone = 0x96;
    public static final int onChargeUpSkip = 0x97;
    public static final int onChargeUpEnd = 0x98;
    public static final int onCheckSemiInvulnerable = 0x99;
    public static final int onHeldItemCheck = 0x9A;
    public static final int onHeldItemFail = 0x9B;
    public static final int onHeldItemDecide = 0x9C;
    public static final int onItemRewriteDone = 0x9D;
    public static final int onCallFieldEffect = 0x9E;
    public static final int onCheckSideConditionParam = 0x9F;
    public static final int onUncategorizedMove = 0xA0;
    public static final int onUncategorizedMoveNoTarget = 0xA1;
    public static final int onCombinedMoveCheck = 0xA2;
    public static final int onSwitchOut = 0xA3;
    public static final int onPostMove = 0xA4;
}
