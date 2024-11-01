    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r0, #VAR_MoveCategory
    bl      Battle::EventVar_GetValue
    cmp     r0, #CAT_Special
    bne     Return
    
    mov     r0, #VAR_Ratio ; stat
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}