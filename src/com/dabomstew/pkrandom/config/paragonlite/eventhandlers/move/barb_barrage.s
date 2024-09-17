    push    {r3-r5, lr}
    mov     r4, r1
    mov     r5, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    mov     r1, #MC_Poison
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     Return
    
    mov     r0, #2
    bl      Battle::CommonMultiplyBasePower
    
Return:
    pop     {r3-r5, pc}
    