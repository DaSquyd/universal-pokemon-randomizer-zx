    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #CAT_Status
    bne     Return
    
    mov     r0, #VAR_NoEffectFlag
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, lr}