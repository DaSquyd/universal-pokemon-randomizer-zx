    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_Bug
    beq     ApplyMod
    cmp     r0, #TYPE_Ghost
    bne     Return
    
ApplyMod:
    mov     r0, #VAR_Ratio
    mov     r1, #((0x1000 * 0.5) >> 8)
    lsl     r1, #8
    bl      Battle::EventVar_MulValue

Return:
    pop     {r4, pc}