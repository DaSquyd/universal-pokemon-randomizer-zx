    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_Stat
    bl      Battle::EventVar_GetValue
    cmp     r0, #BPV_AttackStat
    bne     Return
    
    mov     r0, #VAR_CritStatFlag
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, pc}