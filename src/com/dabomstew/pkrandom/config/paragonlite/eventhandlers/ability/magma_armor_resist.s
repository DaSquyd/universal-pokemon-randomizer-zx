; 0x3B
    push    {r4, lr}
    mov     r0, #4
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #0x16 ; move type
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r0, #24
    cmp     r0, #10 ; Water-type
    beq     ApplyMod
    cmp     r0, #4 ; Ground-type
    bne     End
    
ApplyMod:
    mov     r0, #0x35 ; offensive stat
    ldr     r1, =2048 ; 0.5x
    bl      Battle::EventVar_MulValue

End:
    pop     {r4, pc}