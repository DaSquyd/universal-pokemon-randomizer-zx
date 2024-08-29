    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x30
    bl      Battle::EventVar_GetValue
    cmp     r0, #60
    bcs     Return ; >= 60
    
    mov     r0, #0x31
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}