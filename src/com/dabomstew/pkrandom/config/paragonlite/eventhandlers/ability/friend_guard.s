    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r0, r4
    beq     Return
    
    mov     r0, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, #VAR_Ratio
    ldr     r1, =(0x1000 * ABILITY_FRIEND_GUARD_MULTIPLIER)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}