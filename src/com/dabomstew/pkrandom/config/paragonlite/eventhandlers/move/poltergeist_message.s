    push    {r3-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    
    
    
Return:
    pop     {r3-r6, pc}