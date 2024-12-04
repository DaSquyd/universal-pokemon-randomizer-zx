    push    {r4-r5, lr}
    mov     r4, r2
    mov     r5, r3
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ldr     r0, [r5, #0x04]
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #FALSE
    str     r0, [r5, #0x00]
    
Return:
    pop     {r4-r5, pc}