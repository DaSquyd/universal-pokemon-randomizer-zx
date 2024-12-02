    push    {r3-r6, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    str     r0, [r6, #0x00]
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    bl      Battle::Poke_GetHeldItem
    str     r0, [r6, #0x04]
    cmp     r0, #0 ; no item
    bne     Return
    
    mov     r0, #VAR_NoEffectFlag
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue
    
    
Return:
    pop     {r3-r6, pc}