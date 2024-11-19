    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    mov     r1, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, #VAR_Ratio
    ldr     r1, =(0x1000 * 1.1)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}