    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    mov     r6, r0
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::GetPokeType
    mov     r4, r0
    
    mov     r0, r6
    bl      Battle::GetPokeType
    mov     r1, r0
    mov     r0, r4
    bl      Battle::TypePair_HasSharedType
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #VAR_MovePower
    ldr     r1, =(0x1000 * ABILITY_RIVALRY_MULTIPLIER)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r6, pc}
