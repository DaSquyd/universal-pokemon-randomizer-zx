    push    {r4-r7, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r6, #4
    lsl     r6, #10
    mov     r7, r6
    
CheckFire:
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_Fire
    bne     CheckContact
    
    lsl     r6, #1 ; x2.0
    
CheckContact:
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r1, #MF_Contact
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     ApplyModifier
    
    lsr     r6, #1 ; x0.5
    
ApplyModifier:
    cmp     r6, r7
    beq     Return

    mov     r0, #VAR_Ratio
    mov     r1, r6
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r7, pc}