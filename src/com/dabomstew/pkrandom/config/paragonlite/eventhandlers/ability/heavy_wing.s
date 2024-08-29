    push    {r4, lr}
    mov     r0, #3
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #0x16 ; move type
    bl      Battle::EventVar_GetValue
    cmp     r0, #2 ; Flying-type
    bne     End
    
    mov     r0, #0x35
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4, pc}