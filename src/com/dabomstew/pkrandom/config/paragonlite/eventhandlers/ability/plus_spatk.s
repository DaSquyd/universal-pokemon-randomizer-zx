    push    {r4-r6, lr}
    mov     r4, r2
    
#if ABILITY_MINUS_KEEP_OLD_EFFECT
    mov     r5, r1
    mov     r6, r3
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     NewEffect
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, r6
    mov     r3, #58 ; Minus
    bl      Battle::CommonPokeHasAbility
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #CAT_Special
    bne     Return
    
    ldr     r1, =(0x1000 * 1.5)
    b       ApplyMul
#endif
    
NewEffect:
#if !ABILITY_MINUS_KEEP_OLD_EFFECT
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    beq     Return
#endif
    
    mov     r0, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #CAT_Special
    bne     Return
    
    ldr     r1, =(0x1000 * ABILITY_PLUS_MULTIPLIER)
    
ApplyMul:
    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_MulValue

Return:
    pop     {r4-r6, pc}