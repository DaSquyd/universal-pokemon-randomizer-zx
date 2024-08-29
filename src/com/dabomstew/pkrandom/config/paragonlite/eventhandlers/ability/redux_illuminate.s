    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #0x04
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x16
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r0, #24
    cmp     r0, #7 ; Ghost
    beq     ApplyDamageReduction
    
    cmp     r0, #16 ; Dark
    bne     Return
    
ApplyDamageReduction:
    mov     r0, #0x35
    mov     r1, #2
    lsl     r1, #10 ; 2048 (0.5x)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}