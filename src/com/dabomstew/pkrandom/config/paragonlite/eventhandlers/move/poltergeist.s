    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    bl      Battle::GetPoke
    bl      Battle::Poke_GetHeldItem
    cmp     r0, #0
    bne     Return
    
    mov     r0, #VAR_NoEffectFlag
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r3-r5, pc}