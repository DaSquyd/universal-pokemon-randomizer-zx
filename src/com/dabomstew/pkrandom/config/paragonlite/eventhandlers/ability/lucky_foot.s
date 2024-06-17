    push    {r4, lr}
    mov     r0, #3
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #18
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    
    mov     r1, #14 ; Kick move flag
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     End
    
    ldr     r1, =6144 ; 1.5x
    mov     r0, #49 ; move power
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4, pc}