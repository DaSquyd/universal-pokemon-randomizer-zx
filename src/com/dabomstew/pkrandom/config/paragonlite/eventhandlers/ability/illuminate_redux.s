    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_Ghost
    beq     ApplyDamageReduction
    
    cmp     r0, #TYPE_Dark
    bne     Return
    
ApplyDamageReduction:
    mov     r0, #VAR_Ratio
    ldr     r1, =(0x1000 * 0.5) ; TODO: we'll change this later to make it customizable
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}
    