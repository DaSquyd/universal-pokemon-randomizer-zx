    push    {r3-r7, lr}
    mov     r5, r0
    mov     r4, r1
    mov     r6, r2
    mov     r7, r3
    
    bl      Battle::EventVar_Push
    
    mov     r0, r4
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_MoveSerial
    mov     r1, r6
    bl      Battle::EventVar_SetConstValue
    
    ; NEW
    mov     r0, #VAR_IntimidateFlag
    mov     r1, r7
    bl      Battle::EventVar_SetConstValue
    ; ~NEW
    
    mov     r0, r5
    mov     r1, #EVENT_OnStatStageChangeFail
    bl      Battle::Event_CallHandlers
    bl      Battle::EventVar_Pop
    pop     {r3-r7, pc}