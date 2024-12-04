    push    {r4, lr}
    mov     r0, #3
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0 ; maybe to ensure same user? same move?
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    lsl     r0, r0, #16
    lsr     r0, r0, #16
    
    mov     r1, #MF_Bite
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, #VAR_MovePower
#if PARAGONLITE
    ldr     r1, =(0x1000 * 1.3)
#else
    mov     r1, #((0x1000 * 1.5) >> 10)
    lsl     r1, #10
#endif
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}