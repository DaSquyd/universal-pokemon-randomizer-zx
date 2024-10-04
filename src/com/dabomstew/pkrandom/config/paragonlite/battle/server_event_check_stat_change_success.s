#DEFINE PUSH_STACK (4 * 6) ; r3-r7, lr
#DEFINE STACK_OFFSET (PUSH_STACK)

#DEFINE VAR_SERVER_FLOW 0x00

#DEFINE ARG_VOLUME (STACK_OFFSET + 0x00)
#DEFINE ARG_MOVE_SERIAL (STACK_OFFSET + 0x04)
#DEFINE ARG_INTIMIDATE_FLAG (STACK_OFFSET + 0x08)

    push    {r3-r7, lr}
    str     r0, [sp, #VAR_SERVER_FLOW]
    mov     r5, r1
    mov     r6, r2
    mov     r7, r3
    
    bl      Battle::EventVar_Push
    
    mov     r0, r5
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_AttackingPoke
    mov     r1, r7
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_MoveEffect
    mov     r1, r6
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #ARG_VOLUME]
    mov     r0, #VAR_Volume
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #ARG_MOVE_SERIAL]
    mov     r0, #VAR_MoveSerial
    bl      Battle::EventVar_SetConstValue
    
    ; NEW
    ldr     r1, [sp, #ARG_INTIMIDATE_FLAG]
    mov     r0, #VAR_IntimidateFlag
    bl      Battle::EventVar_SetConstValue
    ; ~NEW
    
    mov     r0, #VAR_MoveFailFlag
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    ldr     r0, [sp, #VAR_SERVER_FLOW]
    mov     r1, #EVENT_OnStatStageChangeLastCheck
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_MoveFailFlag
    bl      Battle::EventVar_GetValue
    
    mov     r6, r0
    bl      Battle::EventVar_Pop
    
    mov     r5, #0
    cmp     r6, #0
    bne     Return
    mov     r5, #1

Return:
    mov     r0, r5
    pop     {r3-r7, pc}
    