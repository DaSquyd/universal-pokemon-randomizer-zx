    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #4
    bl      Battle::EventVar_GetValue
    cmp     r0, r0
    bne     End
    
    mov     r0, #0x41
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue

End:
    pop     {r1, pc}