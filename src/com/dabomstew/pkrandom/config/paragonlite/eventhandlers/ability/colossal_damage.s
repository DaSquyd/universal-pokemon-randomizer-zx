    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_Ratio
    ldr     r1, =3277 ; 80%
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}