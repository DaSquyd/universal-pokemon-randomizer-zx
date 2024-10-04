#DEFINE PUSH_STACK (4 * 6) ; r3-r7, lr
#DEFINE STACK_OFFSET (PUSH_STACK)

#DEFINE ARG_VOLUME (STACK_OFFSET + 0x00)
#DEFINE ARG_INTIMIDATE_FLAG (STACK_OFFSET + 0x04)

    push    {r3-r7, lr}
    mov     r5, r0
    mov     r4, r1
    mov     r6, r2
    mov     r7, r3
    bl      Battle::EventVar_Push
    
    mov     r0, #VAR_AttackingPoke
    mov     r1, r4
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_MoveEffect
    mov     r1, r7
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #ARG_VOLUME]
    mov     r0, #VAR_Volume
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #ARG_INTIMIDATE_FLAG]
    mov     r0, #VAR_IntimidateFlag
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, r5
    mov     r1, #EVENT_OnStatStageChangeSuccess
    bl      Battle::Event_CallHandlers
    bl      Battle::EventVar_Pop
    pop     {r3-r7, pc}
    