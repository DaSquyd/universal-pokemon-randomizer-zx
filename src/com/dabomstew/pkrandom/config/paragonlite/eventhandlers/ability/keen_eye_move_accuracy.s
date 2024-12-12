    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_Ratio
    ldr     r1, =(0x1000 * 1.1)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}