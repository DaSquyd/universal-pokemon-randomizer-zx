    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_Dragon
    bne     Return
    
    mov     r0, #VAR_Ratio
    mov     r1, #(0x1800 >> 10) ; 1.5x
    lsl     r1, #10
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}