    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x35
    mov     r1, #0x09 ; Defense
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4, pc}