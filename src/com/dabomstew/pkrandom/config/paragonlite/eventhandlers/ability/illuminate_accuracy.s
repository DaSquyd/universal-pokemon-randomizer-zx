    push    {r3, lr}
    mov     r0, #VAR_Ratio
    ldr     r1, =(4096 * ABILITY_ILLUMINATE_ACCURACY_MULTIPLIER)
    bl      Battle::EventVar_MulValue
    pop     {r3, pc}