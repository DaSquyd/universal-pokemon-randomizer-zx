    push    {r4-r5, lr}    
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    bl      Battle::GetPokeType
    bl      Battle::TypePair_GetType1
    mov     r1, r0
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4-r5, pc}