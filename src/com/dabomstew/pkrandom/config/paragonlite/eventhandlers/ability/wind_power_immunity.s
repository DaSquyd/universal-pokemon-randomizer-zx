    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #4
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    ; Get Move ID
    mov     r0, #0x12 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, r0, #16
    lsr     r0, r0, #16
    
    mov     r1, #19 ; wind move flag
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     End
    
    mov     r0, #0x40
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    cmp     r0, #0
    beq     End
    
; apply charge
    mov     r0, r5
    
    ; 1200 Being hit by [move] charged [poke] with power!
    
End:
    pop     {r4-r6, pc}
