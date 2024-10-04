    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r1, #MF_Wind
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_MovePower
    ldr     r1, =5325 ; 1.3x
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}