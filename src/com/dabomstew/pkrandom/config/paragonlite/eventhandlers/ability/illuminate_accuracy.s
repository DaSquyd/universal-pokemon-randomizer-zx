    mov     r0, #VAR_Ratio
    ldr     r1, =(4096 * 1.3)
    bl      Battle::EventVar_MulValue
    bx      lr