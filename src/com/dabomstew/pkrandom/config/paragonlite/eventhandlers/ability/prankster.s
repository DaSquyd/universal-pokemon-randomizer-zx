    push    {r4-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #0
    bne     Return
    
    mov     r0, #VAR_MovePriority
    bl      Battle::EventVar_GetValue
    add     r1, r0, #1
    mov     r0, #VAR_MovePriority
    bl      Battle::EventVar_RewriteValue
    
    ; NEW - Prankster flag
    ldr     r0, =ServerFlow.moveParam
    ldr     r0, [r5, r0]
    ldr     r1, [r0, #MoveParam.flags]
    mov     r2, #MPF_Prankster
    orr     r1, r2
    str     r1, [r0, #MoveParam.flags]
    
Return:
    pop     {r4-r5, pc}