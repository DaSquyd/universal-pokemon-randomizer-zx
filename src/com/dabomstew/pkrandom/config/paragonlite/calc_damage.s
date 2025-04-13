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
#DEFINE ARG_SKIP_RANDOMNESS (STACK_SIZE + 0x0C)
#DEFINE ARG_DEST_DAMAGE_PTR (STACK_SIZE + 0x10)

    push    {r4-r7, lr}
    sub     sp, #ADD_STACK_SIZE
    mov     r5, r0 ; r5 := *serverFlow
    str     r1, [sp, #SP_ATTACKING_MON]
    str     r2, [sp, #SP_DEFENDING_MON]
    mov     r4, r3 ; r4 := *moveParam
    
    ldr     r6, [sp, #ARG_IS_CRITICAL_HIT]
    
    ldrh    r0, [r4, #MoveParam.moveId]
    bl      ARM9::GetMoveCategory
    str     r0, [sp, #SP_MOVE_CATEGORY]
    
    mov     r0, #0
    str     r0, [sp, #SP_IS_FIXED_DAMAGE]
    
    bl      Battle::EventVar_Push
    
DamageProcess1:
    ldr     r1, [sp, #ARG_EFFECTIVENESS]
    mov     r0, #VAR_Effectiveness
    bl      Battle::EventVar_SetConstValue
    
    ldr     r0, [sp, #SP_ATTACKING_MON]
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_SetConstValue
    
    ldr     r0, [sp, #SP_DEFENDING_MON]
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_CriticalFlag
    mov     r1, r6
    bl      Battle::EventVar_SetConstValue
    
    ldrb    r1, [r4, #MoveParam.moveType]
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_SetConstValue
    
    ldrh    r1, [r4, #MoveParam.moveId]
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #SP_MOVE_CATEGORY]
    mov     r0, #VAR_MoveCategory
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_FixedDamage
    mov     r1, #0
    bl      Battle::EventVar_SetValue
    
    mov     r0, r5
    mov     r1, #EVENT_OnMoveDamageProcessing1
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_FixedDamage
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
    mov     r1, #BPV_Level
    bl      Battle::GetPokeStat
    mov     r2, r0 ; r2 := level
    ldr     r0, [sp, #SP_POWER]
    ldr     r1, [sp, #SP_OFFENSIVE_VALUE]
    ldr     r3, [sp, #SP_DEFENSIVE_VALUE]
    bl      Battle::CalcBaseDamage
    mov     r7, r0
    
DamageRatio:
    ldr     r0, [sp, #ARG_TARGET_DMG_RATIO]
    ldr     r1, =0x1000
    cmp     r0, r1
    beq     Weather
    mov     r1, r7
    bl      Battle::FixedRound
    mov     r7, r0
    
Weather:
    mov     r0, r5
    ldr     r1, [sp, #SP_ATTACKING_MON]
    bl      Battle::GetEffectiveWeather
    ldrb    r1, [r4, #MoveParam.moveType]
    bl      Battle::ServerEvent_WeatherPowerMod
    ldr     r1, =0x1000
    cmp     r0, r1
    beq     CriticalHit
    mov     r1, r7
    bl      Battle::FixedRound
    mov     r7, r0
    
CriticalHit:
    cmp     r6, #0
    beq     Debug
    
    ; small optimizations
#if Math.log2(CRITICAL_HIT_MULTIPLIER) % 1 === 0
    ; power of 2
    lsl     r7, #Math.log2(CRITICAL_HIT_MULTIPLIER)
#elif CRITICAL_HIT_MULTIPLIER == 1.5
    ; modern
    lsr     r0, r7, #1
    add     r7, r0
#elif CRITICAL_HIT_MULTIPLIER == 1.25
    ; modern
    lsr     r0, r7, #2
    add     r7, r0
#elif CRITICAL_HIT_MULTIPLIER == Math.round(CRITICAL_HIT_MULTIPLIER)
    ; whole number multiplier, not power of 2
    ldr     r0, =CRITICAL_HIT_MULTIPLIER
    mul     r7, r0
#else
    ; fractional multiplier -> use percentage
    ldr     r0, =(100 * CRITICAL_HIT_MULTIPLIER)
    mul     r0, r7
    mov     r1, #100
    blx     ARM9::DivideModUnsigned
    mov     r7, r0
#endif
    
Debug:
;    ldr     r0, [r5, #ServerFlow.mainModule]
;    mov     r1, #DEBUG_NoRandDamage
;    bl      Battle::MainModule_GetDebugFlag
;    cmp     r0, #0
;    bne     STAB
    
#if HAS_POKESTAR_STUDIOS
    mov     r0, r5
    bl      Battle::ServerFlow_IsNotPokestarStudios
    cmp     r0, #FALSE
    beq     STAB
#endif
    
    ldr     r0, [sp, #ARG_SKIP_RANDOMNESS]
    cmp     r0, #FALSE
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
    ldrb    r2, [r4, #MoveParam.moveType]
    cmp     r2, #18 ; null type
    beq     Effectiveness
    
    ldr     r1, [sp, #SP_ATTACKING_MON]
    mov     r0, r5
    bl      Battle::ServerEvent_STAB
    mov     r1, r0
    mov     r0, r7
    bl      Battle::FixedRound
    mov     r7, r0
    
Effectiveness:
    ldr     r1, [sp, #ARG_EFFECTIVENESS]
    mov     r0, r7
    bl      Battle::ServerEvent_GetEffectivenessMod
    mov     r7, r0
    
#if REPLACE_FREEZE_WITH_FROSTBITE
CheckSpecial:
    ; Special
    ldr     r0, [sp, #SP_MOVE_CATEGORY]
    cmp     r0, #CAT_Special
    bne     CheckPhysical
    
    ; Frostbite
    ldr     r0, [sp, #SP_ATTACKING_MON]
    bl      Battle::GetPokeStatus
    cmp     r0, #MC_Frostbite
    bne     ZeroHandle
    
    ; Coolant Boost
    ldr     r0, [sp, #SP_ATTACKING_MON]
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    ldr     r1, =ABILITY_COOLANT_BOOST
    cmp     r0, r1
    bne     Condition_HalveDamage
    b       ZeroHandle
#endif
    
CheckPhysical:
    ; Physical
    ldr     r0, [sp, #SP_MOVE_CATEGORY]
    cmp     r0, #CAT_Physical
    bne     ZeroHandle
    
    ; Burn
    ldr     r0, [sp, #SP_ATTACKING_MON]
    bl      Battle::GetPokeStatus
    cmp     r0, #MC_Burn
    bne     ZeroHandle
    
    ; Guts
    ldr     r0, [sp, #SP_ATTACKING_MON]
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    cmp     r0, #62 ; Guts
    beq     ZeroHandle
    
Condition_HalveDamage:
    ; Facade
    ldr     r0, [r4, #MoveParam.moveId]
    sub     r0, #(263 - 0xFF) ; Facade
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
    mov     r0, #VAR_Ratio
    mov     r2, #(4096 * 0.01) ; (0.01x) min
    lsl     r3, r1, #5 ; (5x) max
    bl      Battle::EventVar_SetMulValue
    
    mov     r0, #VAR_Damage
    mov     r1, r7
    bl      Battle::EventVar_SetValue
    
    mov     r0, r5
    mov     r1, #EVENT_OnMoveDamageProcessing2
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_Damage
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    
    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_GetValue
    mov     r1, r7
    bl      Battle::FixedRound
    mov     r7, r0
    
CallEndProcess:
    mov     r0, r5
    mov     r1, #EVENT_OnMoveDamageProcessingEnd
    bl      Battle::Event_CallHandlers
    
    bl      Battle::EventVar_Pop
    
    ldr     r1, [sp, #ARG_DEST_DAMAGE_PTR]
    strh    r7, [r1]
    
Return:
    ldr     r0, [sp, #SP_IS_FIXED_DAMAGE]
    add     sp, #ADD_STACK_SIZE
    pop     {r4-r7, pc}
    