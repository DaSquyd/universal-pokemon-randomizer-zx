    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_EvasionStage
    bl      Battle::EventVar_GetValue
    cmp     r0, #6 ; +0
    bls     Return
    
    mov     r1, #6 ; +0
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, pc}