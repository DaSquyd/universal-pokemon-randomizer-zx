; 0x3C
    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #4
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #18 ; move id
    bl      Battle::EventVar_GetValue
    mov     r0, #26 ; move category
    bl      Battle::EventVar_GetValue
    cmp     r0, #1 ; physical
    bne     End
    
    mov     r0, #53 ; stat
    mov     r1, #(0x1800 >> 10)
    lsl     r1, #10
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4, pc}