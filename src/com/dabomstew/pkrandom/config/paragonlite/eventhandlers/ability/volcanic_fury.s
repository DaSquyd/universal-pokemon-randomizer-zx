    push    {r4-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_ConditionId
    mov     r1, #MC_Burn
    bl      Battle::EventVar_RewriteValue
    
    mov     r0, #MC_Burn
    bl      Battle::MakeNonVolatileStatus
    mov     r1, r0
    mov     r0, #VAR_ConditionAddress
    bl      Battle::EventVar_RewriteValue
    
    mov     r0, #VAR_EffectChance
    mov     r1, #100
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4-r5, pc}