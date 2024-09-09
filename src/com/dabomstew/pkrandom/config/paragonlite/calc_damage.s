#DEFINE PUSH_STACK_SIZE 0x14 ; {r4-r7, lr}

#DEFINE SP_VAR_0 0x00
#DEFINE SP_ATTACKING_MON 0x04
#DEFINE SP_DEFENDING_MON 0x08
#DEFINE SP_IS_FIXED_DAMAGE 0x0C
#DEFINE SP_MOVE_CATEGORY 0x10
#DEFINE SP_POWER 0x14
#DEFINE SP_OFFENSIVE_VALUE 0x18
#DEFINE SP_DEFENSIVE_VALUE 0x1C
#DEFINE ADD_STACK_SIZE 0x20

#DEFINE STACK_SIZE (PUSH_STACK_SIZE + ADD_STACK_SIZE)

#DEFINE ARG_EFFECTIVENESS (STACK_SIZE + 0x00)
#DEFINE ARG_TARGET_DMG_RATIO (STACK_SIZE + 0x04)
#DEFINE ARG_IS_CRITICAL_HIT (STACK_SIZE + 0x08)
#DEFINE ARG_IS_DEBUG_MODE (STACK_SIZE + 0x0C)
#DEFINE ARG_DEST_DAMAGE_PTR (STACK_SIZE + 0x10)

    push    {r4-r7, lr}
    sub     sp, #ADD_STACK_SIZE
    mov     r5, r0 ; r5 := *serverFlow
    str     r1, [sp, #SP_ATTACKING_MON]
    str     r2, [sp, #SP_DEFENDING_MON]
    mov     r4, r3 ; r4 := *moveParam
    
    ldr     r6, [sp, #ARG_IS_CRITICAL_HIT]
    
    ldrh    r0, [r4, #MOVE_PARAM_MOVE_ID]
    bl      ARM9::GetMoveCategory
    str     r0, [sp, #SP_MOVE_CATEGORY]
    
    mov     r0, #0
    str     r0, [sp, #SP_IS_FIXED_DAMAGE]
    
    bl      Battle::EventVar_Push
    
DamageProcess1:
    ldr     r1, [sp, #ARG_EFFECTIVENESS]
    mov     r0, #VAR_TYPE_EFFECTIVENESS
    bl      Battle::EventVar_SetConstValue
    
    ldr     r0, [sp, #SP_ATTACKING_MON]
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_SetConstValue
    
    ldr     r0, [sp, #SP_DEFENDING_MON]
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_DEFENDING_MON
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_CRITICAL_FLAG
    mov     r1, r6
    bl      Battle::EventVar_SetConstValue
    
    ldrb    r1, [r4, #MOVE_PARAM_MOVE_TYPE]
    mov     r0, #VAR_MOVE_TYPE
    bl      Battle::EventVar_SetConstValue
    
    ldrh    r1, [r4, #MOVE_PARAM_MOVE_ID]
    mov     r0, #VAR_MOVE_ID
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #SP_MOVE_CATEGORY]
    mov     r0, #VAR_MOVE_CATEGORY
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_FIXED_DAMAGE
    mov     r1, #0
    bl      Battle::EventVar_SetValue
    
    mov     r0, r5
    mov     r1, #EVENT_MOVE_DAMAGE_PROCESSING_1
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_FIXED_DAMAGE
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    cmp     r0, #0
    beq     MovePower
    
    mov     r0, #1
    str     r0, [sp, #SP_IS_FIXED_DAMAGE]
    b       CallEndProcess
    
MovePower:
    mov     r0, r5 ; r0 := *serverFlow
    ldr     r1, [sp, #SP_ATTACKING_MON]
    ldr     r2, [sp, #SP_DEFENDING_MON]
    mov     r3, r4 ; r3 := *moveParam
    bl      Battle::ServerEvent_GetMovePower
    str     r0, [sp, #SP_POWER]
    
OffensiveValue:
    mov     r0, r5 ; r0 := *serverFlow
    ldr     r1, [sp, #SP_ATTACKING_MON]
    ldr     r2, [sp, #SP_DEFENDING_MON]
    mov     r3, r4 ; r3 := *moveParam
    str     r6, [sp, #SP_VAR_0] ; isCriticalHit
    bl      Battle::ServerEvent_GetOffensiveValue
    str     r0, [sp, #SP_OFFENSIVE_VALUE]
    
DefensiveValue:
    mov     r0, r5 ; r0 := *serverFlow
    ldr     r1, [sp, #SP_ATTACKING_MON]
    ldr     r2, [sp, #SP_DEFENDING_MON]
    mov     r3, r4 ; r3 := *moveParam
    str     r6, [sp, #SP_VAR_0] ; isCriticalHit
    bl      Battle::ServerEvent_GetDefensiveValue
    str     r0, [sp, #SP_DEFENSIVE_VALUE]
    
BaseDamage:
    ; level
    ldr     r0, [sp, #SP_ATTACKING_MON]
    mov     r1, #BATTLE_STAT_LEVEL
    bl      Battle::GetPokeStat
    mov     r2, r0 ; r2 := level
    ldr     r0, [sp, #SP_POWER]
    ldr     r1, [sp, #SP_OFFENSIVE_VALUE]
    ldr     r3, [sp, #SP_DEFENSIVE_VALUE]
    bl      Battle::CalcBaseDamage
    mov     r7, r0
    
DamageRatio:
    ldr     r0, [sp, #ARG_TARGET_DMG_RATIO]
    mov     r1, #1
    lsl     r1, #12 ; 4096
    cmp     r0, r1
    beq     Weather
    mov     r1, r7
    bl      Battle::FixedRound
    mov     r7, r0
    
Weather:
    mov     r0, r5
    bl      Battle::ServerEvent_GetWeather
    ldrb    r1, [r4, #MOVE_PARAM_MOVE_TYPE]
    bl      Battle::ServerEvent_WeatherPowerMod
    mov     r1, #1
    lsl     r1, #12 ; 4096
    cmp     r0, r1
    beq     CriticalHit
    mov     r1, r7
    bl      Battle::FixedRound
    mov     r7, r0
    
CriticalHit:
    cmp     r6, #0
    beq     Debug
    
    lsr     r0, r7, #1
    add     r7, r0
    
Debug:
;    ldr     r0, [r5, #SERVER_FLOW_MAIN_MODULE]
;    mov     r1, #DEBUG_NO_RAND_DAMAGE
;    bl      Battle::MainModule_GetDebugFlag
;    cmp     r0, #0
;    bne     STAB
    
    ; TODO: B2W2 only
    mov     r0, r5
    bl      Battle::ServerFlow_IsCompetitorScenarioMode
    cmp     r0, #0
    beq     STAB
    ; TODO: ~B2W2 only
    
    ldr     r0, [sp, #ARG_IS_DEBUG_MODE]
    cmp     r0, #0
    beq     Random
    
    mov     r0, #85
    b       Random_Multiply
    
Random:
    mov     r0, #16
    bl      Battle::Random
    mov     r1, #100
    sub     r0, r1, r0
    
Random_Multiply:
    mul     r0, r7
    mov     r1, #100
    
Random_Divide:
    blx     ARM9::DivideModUnsigned
    mov     r7, r0
    
STAB:
    ldrb    r2, [r4, #MOVE_PARAM_MOVE_TYPE]
    cmp     r2, #18 ; null type
    beq     Effectiveness
    
    ldr     r1, [sp, #SP_ATTACKING_MON]
    mov     r0, r5
    bl      Battle::ServerEvent_SameTypeAttackBonus
    mov     r1, r0
    mov     r0, r7
    bl      Battle::FixedRound
    mov     r7, r0
    
Effectiveness:
    ldr     r1, [sp, #ARG_EFFECTIVENESS]
    mov     r0, r7
    bl      Battle::ServerEvent_GetEffectivenessMod
    mov     r7, r0
    
Condition_Frostbite:
    ; Special
    ldr     r0, [sp, #SP_MOVE_CATEGORY]
    cmp     r0, #MOVE_CATEGORY_SPECIAL
    bne     Condition_Burn
    
    ; Frostbite
    ldr     r0, [sp, #SP_ATTACKING_MON]
    bl      Battle::GetPokeStatus
    cmp     r0, #CONDITION_FROSTBITE
    beq     Condition_HalveDamage
    
Condition_Burn:
    ; Physical
    ldr     r0, [sp, #SP_MOVE_CATEGORY]
    cmp     r0, #MOVE_CATEGORY_PHYSICAL
    bne     ZeroHandle
    
    ; Burn
    ldr     r0, [sp, #SP_ATTACKING_MON]
    bl      Battle::GetPokeStatus
    cmp     r0, #CONDITION_BURN
    bne     ZeroHandle
    
    ; Guts
    ldr     r0, [sp, #SP_ATTACKING_MON]
    mov     r1, #BATTLE_STAT_EFFECTIVE_ABILITY
    bl      Battle::GetPokeStat
    cmp     r0, #ABILITY_062_GUTS
    beq     ZeroHandle
    
Condition_HalveDamage:
    ; Facade
    ldr     r0, [r4, #MOVE_PARAM_MOVE_ID]
    sub     r0, #(MOVE_263_FACADE - 0xFF)
    cmp     r0, #0xFF
    beq     ZeroHandle
    
    lsr     r7, #1
    
ZeroHandle:
    cmp     r7, #0
    bne     FinalDamage
    
    mov     r7, #1
    
FinalDamage:
    mov     r1, #1
    lsl     r1, #12
    mov     r0, #VAR_RATIO
    mov     r2, #(Math.round(4096 * 0.01)) ; (0.01x) min
    lsl     r3, r1, #5 ; (5x) max
    bl      Battle::EventVar_SetMulValue
    
    mov     r0, #VAR_DAMAGE
    mov     r1, r7
    bl      Battle::EventVar_SetValue
    
    mov     r0, r5
    mov     r1, #EVENT_MOVE_DAMAGE_PROCESSING_2
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_DAMAGE
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    
    mov     r0, #VAR_RATIO
    bl      Battle::EventVar_GetValue
    mov     r1, r7
    bl      Battle::FixedRound
    mov     r7, r0
    
CallEndProcess:
    mov     r0, r5
    mov     r1, #EVENT_MOVE_DAMAGE_PROCESSING_END
    bl      Battle::Event_CallHandlers
    
    bl      Battle::EventVar_Pop
    
    ldr     r1, [sp, #ARG_DEST_DAMAGE_PTR]
    strh    r7, [r1]
    
Return:
    ldr     r0, [sp, #SP_IS_FIXED_DAMAGE]
    add     sp, #ADD_STACK_SIZE
    pop     {r4-r7, pc}