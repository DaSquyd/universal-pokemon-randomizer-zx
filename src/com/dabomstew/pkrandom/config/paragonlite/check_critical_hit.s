#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_DEFENDING_MON 0x04
#DEFINE VAR_CRIT_STAGE 0x2C
#DEFINE VAR_MOVE_FAIL_FLAG 0x41

#DEFINE EVENT_CRITICAL_CHECK 0x36

#DEFINE MVDATA_CRIT_STAGE 0x07

#DEFINE MAX_CRIT_STAGE 3

#DEFINE MOVE_ID 0x00 ; r3 on the stack

; r0: *serverFlow
; r1: *attackingMonParam
; r2: *defendingMonParam
; r3: moveId

    push    {r3-r7, lr}
    mov     r5, r0 ; r5 := *serverFlow
    mov     r6, r1 ; r6 := *attackingMonParam
    mov     r0, r3 ; r0 := moveId
    mov     r1, #MVDATA_CRIT_STAGE
    mov     r7, r2 ; r7 := *defendingMonParam
    
    str     r3, [sp, #MOVE_ID]
    bl      ARM9::GetMoveData
    mov     r4, r0
    
    mov     r0, r6; r0 := *attackingMonParam
    bl      Battle::GetCritStage
    add     r4, r0
    
    bl      Battle::EventVar_Push
    
    mov     r0, r7 ; r0 := *defendingMonParam
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_DEFENDING_MON
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, r6 ; r0 := *attackingMonParam
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_CRIT_STAGE
    mov     r1, r4
    mov     r6, #VAR_CRIT_STAGE
    bl      Battle::EventVar_SetValue
    
    mov     r0, #VAR_MOVE_FAIL_FLAG
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, r5 ; r0 := *serverFlow
    mov     r1, #EVENT_CRITICAL_CHECK
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_MOVE_FAIL_FLAG
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bne     ReturnR4
    
    mov     r4, #0
    ldr     r0, [sp, #MOVE_ID]
    bl      ARM9::IsMoveAlwaysCrit
    cmp     r0, #0
    beq     CheckMaxCritStage
    
    mov     r4, #1
    b       CheckDebug
    
CheckMaxCritStage:
    mov     r0, #VAR_CRIT_STAGE
    bl      Battle::EventVar_GetValue
    mov     r2, r0  ; r2 := critStage
    cmp     r0, #MAX_CRIT_STAGE
    ble     CheckIsSimulation
    mov     r0, #MAX_CRIT_STAGE
    
CheckIsSimulation:
    mov     r0, r5 ; r0 := *serverFlow
    bl      Battle::Handler_IsSimulationMode
    cmp     r0, #0
    beq     RollCriticalHit
    
CheckSimulationThreshold:
    cmp     r2, #2
    blt     CheckDebug
    
SimulationThresholdMet:
    mov     r4, #1
    b       CheckDebug
    
RollCriticalHit:
    bl      Battle::RollCriticalHit
    mov     r4, r0
    
CheckDebug:
    ldr     r0, [r5, #0x04] ; r0 := serverFlow->mainModule
    mov     r1, #3
    bl      Battle::MainModule_GetDebugFlag
    cmp     r0, #0
    beq     ReturnR4
    mov     r4, #1
    
ReturnR4:
    bl      Battle::EventVar_Pop
    mov     r0, r4
    pop     {r3-r7, pc}
    
    
    