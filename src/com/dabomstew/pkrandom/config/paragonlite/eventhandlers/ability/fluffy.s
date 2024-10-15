    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r6, #4
    lsl     r6, #10
    
CheckFire:
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_Fire
    bne     CheckContact
    
#if PARAGONLITE
    lsr     r0, r6, #1
    add     r6, r0 ; x1.5
#else
    lsl     r6, #1 ; x2.0
#endif
    
CheckContact:
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r1, #0 ; contact flag
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     ApplyModifier
    
    lsr     r6, #1 ; x0.5
    
ApplyModifier:
    mov     r0, #4
    lsl     r0, #10 ; 4096
    cmp     r0, r6
    beq     Return

    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r6, pc}