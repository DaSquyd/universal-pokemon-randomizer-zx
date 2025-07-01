    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_Water
    beq     ApplyMod
    cmp     r0, #TYPE_Poison
    bne     Return
    
ApplyMod:
    mov     r0, #VAR_Ratio
    ldr     r1, =(0x1000 * 0.5)
    bl      Battle::EventVar_MulValue

Return:
    pop     {r4, pc}