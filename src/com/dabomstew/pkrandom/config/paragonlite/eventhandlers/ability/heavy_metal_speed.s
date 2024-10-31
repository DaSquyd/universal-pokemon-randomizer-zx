; 0x13
    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #53 ; stat
    ldr     r1, =(4096 * 0.75)
    bl      Battle::EventVar_MulValue

Return:
    pop     {r4, pc}