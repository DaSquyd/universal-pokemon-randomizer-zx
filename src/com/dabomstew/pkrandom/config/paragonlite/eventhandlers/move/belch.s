    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::Poke_GetUsedItem
    cmp     r0, #0
    beq     Fail
    
    bl      ARM9::IsItemBerry ; TODO: update to include new berry items
    cmp     r0, #FALSE
    bne     Return

Fail:
    mov     r0, #VAR_FailCause
    mov     r1, #MFC_Other
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r3-r5, pc}