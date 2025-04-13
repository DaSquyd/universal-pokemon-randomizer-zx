    push    {r4, lr}
    mov     r0, #VAR_AttackingPoke
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0 ; maybe to ensure same user? same move?
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    
    mov     r1, #MF_Slice
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return
    
    ldr     r1, =(0x1000 * ABILITY_SHARPNESS_MULTIPLIER)
    mov     r0, #VAR_MovePower
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}