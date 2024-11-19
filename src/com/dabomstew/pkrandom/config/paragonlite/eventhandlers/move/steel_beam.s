    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    bl      Battle::GetPoke
    mov     r1, #2 ; 1/2
    bl      Battle::DivideMaxHPZeroCheck
    neg     r6, r0
    
    mov     r0, r5
    mov     r1, #HE_ChangeHP
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    ldr     r0, [r1, #HandlerParam_ChangeHP.header]
    mov     r2, #(BHP_FailSkip >> 24)
    lsl     r2, #24
    orr     r0, r2
    str     r0, [r1, #HandlerParam_ChangeHP.header]
    
    mov     r0, #1
    strb    r0, [r1, #HandlerParam_ChangeHP.pokeCount]
    strb    r4, [r1, #HandlerParam_ChangeHP.pokeId]
    str     r6, [r1, #HandlerParam_ChangeHP.amount]
    
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r6, pc}