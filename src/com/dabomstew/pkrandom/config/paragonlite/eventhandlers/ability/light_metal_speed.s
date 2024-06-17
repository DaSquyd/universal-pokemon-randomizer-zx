; 0x13
    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #53 ; stat
    ldr     r1, =4915 ; 1.2x
    bl      Battle::EventVar_MulValue

End:
    pop     {r4, pc}