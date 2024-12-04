    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_Stat
    mov     r1, #BPV_DefenseStat
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, pc}