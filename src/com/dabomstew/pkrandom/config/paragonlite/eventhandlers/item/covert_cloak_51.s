    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #4
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #3
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    beq     End
    mov     r0, #0x41
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r4, pc}