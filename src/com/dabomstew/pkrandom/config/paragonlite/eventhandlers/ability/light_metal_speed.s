; 0x13
    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #53 ; stat
    mov     r1, #(0x1800 >> 10)
    lsl     r1, #10
    bl      Battle::EventVar_MulValue

End:
    pop     {r4, pc}