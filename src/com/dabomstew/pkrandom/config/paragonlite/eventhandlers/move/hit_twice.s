; flags to the system that accuracy should be checked for each hit

#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_AVOID_FLAG 0x42
#DEFINE TRUE 1

    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_AVOID_FLAG
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, pc}