#DEFINE PUSH_STACK_SIZE 0x14
#DEFINE ADD_STACK_SIZE 0x34
#DEFINE STACK_SIZE (PUSH_STACK_SIZE + ADD_STACK_SIZE)

#DEFINE CALC_EFFECTIVENESS 0x00
#DEFINE CALC_TARGET_DMG_RATIO 0x04
#DEFINE CALC_CRITICAL_FLAG 0x08
#DEFINE CALC_DEBUG_MODE 0x0C
#DEFINE CALC_OUT_DAMAGE 0x10
#DEFINE DEFENDING_MON_PARAM 0x14
#DEFINE ATTACKING_MON_PARAM 0x18
#DEFINE OUT_DAMAGE 0x1C
#DEFINE MOVE_PARAM 0x20

#DEFINE ARG_0 (STACK_SIZE + 0x00)
#DEFINE DEBUG_MODE (STACK_SIZE + 0x04)

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
    
    ldr     r0, [r5, #0x08] ; r0 := serverFlow->pokeCon
    mov     r1, r6 ; r1 := attackingMonId
    bl      Battle::PokeCon_GetPoke
    str     r0, [sp, #ATTACKING_MON_PARAM]
    
    ldr     r0, [r5, #0x08] ; r0 := serverFlow->pokeCon
    mov     r1, r7
    bl      Battle::PokeCon_GetPoke
    
    ldr     r1, =0x0774
    str     r0, [sp, #DEFENDING_MON_PARAM]
    ldr     r2, [r5, r1] ; r2 := serverFlow->simulationCounter
    add     r2, #1
    str     r2, [r5, r1] ; serverFlow->simulationCounter += 1
    bl      Battle::IsIllusionEnabled
    cmp     r0, #0
    beq     Effectiveness
    
Illusion:
    ldr     r0, [r5, #0x04] ; r0 := serverFlow->mainModule
    ldr     r1, [r5, #0x08] ; r1 := serverFlow->pokeCon
    ldr     r2, [sp, #DEFENDING_MON_PARAM]
    bl      Battle::GetIllusionDisguise
    str     r0, [sp, #DEFENDING_MON_PARAM]
    
Effectiveness:
    ldr     r0, [sp, #ARG_0]
    cmp     r0, #0
    beq     NeutralEffectiveness
    
    mov     r0, r5 ; r0 := serverFlow
    mov     r1, r6 ; r1 := attackingMonId
    mov     r2, r7 ; r2 := defendingMonId
    mov     r3, r4 ; r3 := moveId
    bl      Battle::Handler_SimulationEffectivenessCore
    mov     r6, r0
    b       GetMoveParam
    
NeutralEffectiveness:
    mov     r6, #EFFECTIVENESS_NEUTRAL
    
GetMoveParam:
    ldr     r2, [sp, #ATTACKING_MON_PARAM] ; r2 := *attackingMonParam
    mov     r0, r5 ; r0 := serverFlow
    mov     r1, r4 ; r1 := moveId
    add     r3, sp, #MOVE_PARAM ; r3 := *moveParam
    bl      Battle::ServerEvent_GetMoveParam
    
CheckDebug:
    ldr     r0, [sp, #DEBUG_MODE]
    mov     r1, #1
    cmp     r0, #0
    beq     CalcDamage
    
    mov     r1, #0
    
CalcDamage:
    mov     r0, #1
    str     r6, [sp, #CALC_EFFECTIVENESS]
    
    lsl     r0, #12
    str     r0, [sp, #CALC_TARGET_DMG_RATIO]
    
    mov     r0, #0
    str     r0, [sp, #CALC_CRITICAL_FLAG]
    
    str     r1, [sp, #CALC_DEBUG_MODE]
    
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
    
    add     r0, sp, #OUT_DAMAGE
    add     sp, #ADD_STACK_SIZE
    ldrh    r0, [r0]
    pop     {r4-r7, pc}
    
ReturnFalse:
    mov     r0, #0
    add     sp, #ADD_STACK_SIZE
    pop     {r4-r7, pc}