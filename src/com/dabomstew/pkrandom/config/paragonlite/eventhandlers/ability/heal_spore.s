    push    {r4-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_SubstituteFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    bne     Return

    mov     r0, r5
    mov     r1, r4
    mov     r2, #16
    bl      Battle::CommonHealAlliesAbility
    
Return:
    pop     {r4-r5, pc}
    