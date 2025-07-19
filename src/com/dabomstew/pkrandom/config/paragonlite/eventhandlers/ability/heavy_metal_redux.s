    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r0, r4
    beq     Return
    
    mov     r0, #VAR_Effectiveness
    bl      Battle::EventVar_GetValue
    bl      Battle::GetEffectivenessAdvantage
    cmp     r0, ADV_SuperEffective
    bne     Return
    
    ldr     r1, =(0x1000 * ABILITY_HEAVY_METAL_MULTIPLIER)
    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}