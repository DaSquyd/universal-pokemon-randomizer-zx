    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0 ; maybe to ensure same user? same move?
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    
    mov     r1, #MF_Bite
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, #VAR_MovePower
    ldr     r1, =(0x1000 * (PARAGONLITE ? 1.2 : 1.3))
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}