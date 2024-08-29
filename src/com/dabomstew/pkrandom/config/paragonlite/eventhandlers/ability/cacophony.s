    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x12 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    
    mov     r1, #8 ; sound move flag
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     Return
    
    mov     r0, #0x31 ; move power
    ldr     r1, =5325 ; 1.3x
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}