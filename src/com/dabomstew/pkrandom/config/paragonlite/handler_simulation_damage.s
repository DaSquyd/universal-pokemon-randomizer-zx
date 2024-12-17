#DEFINE PUSH_STACK_SIZE 0x14
#DEFINE ADD_STACK_SIZE 0x34
#DEFINE STACK_SIZE (PUSH_STACK_SIZE + ADD_STACK_SIZE)

#DEFINE CALC_EFFECTIVENESS 0x00
#DEFINE CALC_TARGET_DMG_RATIO 0x04
#DEFINE CALC_CRITICAL_FLAG 0x08
#DEFINE CALC_SKIP_RANDOMNESS 0x0C
#DEFINE CALC_OUT_DAMAGE 0x10
#DEFINE DEFENDING_MON_PARAM 0x14
#DEFINE ATTACKING_MON_PARAM 0x18
#DEFINE OUT_DAMAGE 0x1C
#DEFINE MOVE_PARAM 0x20

#DEFINE ARG_USE_EFFECTIVENESS (STACK_SIZE + 0x00)
#DEFINE ARG_USE_RANDOMNESS (STACK_SIZE + 0x04)

#DEFINE EFFECTIVENESS_NEUTRAL 3

; r0: serverFlow
; r1: attackingMonId
; r2: defendingMonId
; r3: moveId

    push    {r4-r7, lr}
    sub     sp, #ADD_STACK_SIZE
    mov     r5, r0
    mov     r6, r1
    mov     r7, r2
    mov     r4, r3
    beq     ReturnFalse ; MoveId == 0
    
    mov     r0, r4
    bl      ARM9::IsMoveDamaging
    cmp     r0, #0
    beq     ReturnFalse
    
    ldr     r0, [r5, #ServerFlow.pokeCon] ; r0 := serverFlow->pokeCon
    mov     r1, r6 ; r1 := attackingMonId
    bl      Battle::PokeCon_GetPoke
    str     r0, [sp, #ATTACKING_MON_PARAM]
    
    ldr     r0, [r5, #ServerFlow.pokeCon] ; r0 := serverFlow->pokeCon
    mov     r1, r7
    bl      Battle::PokeCon_GetPoke
    
    ldr     r1, =0x0774
    str     r0, [sp, #DEFENDING_MON_PARAM]
    ldr     r2, [r5, r1] ; r2 := serverFlow->simulationCounter
    add     r2, #1
    str     r2, [r5, r1] ; serverFlow->simulationCounter += 1
    bl      Battle::IsIllusionEnabled
    cmp     r0, #0
    beq     BindMoveEvent
    
Illusion:
    ldr     r0, [r5, #ServerFlow.mainModule] ; r0 := serverFlow->mainModule
    ldr     r1, [r5, #ServerFlow.pokeCon] ; r1 := serverFlow->pokeCon
    ldr     r2, [sp, #DEFENDING_MON_PARAM]
    bl      Battle::GetIllusionDisguise
    str     r0, [sp, #DEFENDING_MON_PARAM]
    
BindMoveEvent:
    ldr     r0, [sp, #ATTACKING_MON_PARAM]
    mov     r1, r4
    mov     r2, #0 ; subPriority
    bl      Battle::MoveEvent_AddItem
    
Effectiveness:
    ldr     r0, [sp, #ARG_USE_EFFECTIVENESS]
    cmp     r0, #0
    beq     NeutralEffectiveness
    
    mov     r0, r5 ; r0 := serverFlow
    mov     r1, r6 ; r1 := attackingMonId
    mov     r2, r7 ; r2 := defendingMonId
    mov     r3, r4 ; r3 := moveId
    bl      Battle::Handler_SimulationEffectivenessCore
    mov     r6, r0
    str     r0, [sp, #CALC_EFFECTIVENESS]
    b       GetMoveParam
    
NeutralEffectiveness:
    mov     r6, #EFFECTIVENESS_NEUTRAL
    
GetMoveParam:
    ldr     r2, [sp, #ATTACKING_MON_PARAM] ; r2 := *attackingMonParam
    mov     r0, r5 ; r0 := serverFlow
    mov     r1, r4 ; r1 := moveId
    add     r3, sp, #MOVE_PARAM ; r3 := *moveParam
    bl      Battle::ServerEvent_GetMoveParam
    
CheckDebugMode:
    ldr     r0, [sp, #ARG_USE_RANDOMNESS]
    mov     r1, #TRUE
    cmp     r0, #FALSE
    beq     StoreDebug
    mov     r1, #FALSE
    
StoreDebug:
    str     r1, [sp, #CALC_SKIP_RANDOMNESS]
    
CriticalHit:
    mov     r0, r5 ; r0 := serverFlow
    ldr     r1, [sp, #ATTACKING_MON_PARAM] ; r1 := *attackingMonParam
    ldr     r2, [sp, #DEFENDING_MON_PARAM] ; r2 := *defendingMonParam
    mov     r3, r4 ; r3 := moveId
    bl      Battle::CheckCriticalHit
    str     r0, [sp, #CALC_CRITICAL_FLAG]
    
Ratio:
    mov     r3, #1
    lsl     r3, #12 ; 4096
    
CheckMultiStrikeMultiplier:
    mov     r0, r5 ; r0 := serverFlow
    ldr     r1, [sp, #ATTACKING_MON_PARAM] ; r1 := *attackingMonParam
    mov     r2, r4 ; r2 := moveId
    bl      Battle::Handler_GetSimulationMultiStrikeMultiplier
    mov     r1, r3
    bl      Battle::FixedRound
    mov     r3, r0
    
CalcDamage:
    str     r3, [sp, #CALC_TARGET_DMG_RATIO]
    
    add     r0, sp, #OUT_DAMAGE
    str     r0, [sp, #CALC_OUT_DAMAGE]
    
    ldr     r1, [sp, #ATTACKING_MON_PARAM] ; r1 := *attackingMonParam
    ldr     r2, [sp, #DEFENDING_MON_PARAM] ; r2 := *defendingMonParam
    mov     r0, r5 ; r0 := serverFlow
    add     r3, sp, #MOVE_PARAM ; r3 := *moveParam
    bl      Battle::ServerEvent_CalcDamage
    ldr     r0, =0x0774
    ldr     r1, [r5, r0] ; r1 := serverFlow->simulationCounter
    sub     r1, #1
    str     r1, [r5, r0] ; serverFlow->SimulationCounter -= 1
    
UnbindMoveEvent:
    ldr     r0, [sp, #ATTACKING_MON_PARAM]
    mov     r1, r4
    bl      Battle::MoveEvent_RemoveItem
    
ReturnDamage:
    add     r0, sp, #OUT_DAMAGE
    ldrh    r0, [r0]
    
    add     sp, #ADD_STACK_SIZE
    pop     {r4-r7, pc}
    
ReturnFalse:
    mov     r0, #0
    add     sp, #ADD_STACK_SIZE
    pop     {r4-r7, pc}