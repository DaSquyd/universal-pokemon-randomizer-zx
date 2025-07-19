    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r0, #VAR_MoveCategory
    bl      Battle::EventVar_GetValue
    cmp     r0, #CAT_Physical
    bne     Return
    
    mov     r0, #VAR_Ratio
    ldr     r1, =(0x1000 * ABILITY_LIGHT_METAL_DEFENSE_MULTIPLIER)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}