    push    {r4, r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #4
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End

    mov     r0, #70 ; is fainted maybe?
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bne     End
    
    mov     r0, #69 ; is crit
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    beq     End
    
    mov     r0, r5
    mov     r1, #14
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    ldr     r2, [r1, #0]
    mov     r0, #1
    lsl     r0, #23
    orr     r0, r2
    str     r0, [r1, #0]
    mov     r0, #1
    strb    r0, [r1, #0x0F]
    strb    r4, [r1, #0x10]
    str     r0, [r1, #0x04]
    strb    r0, [r1, #0x0C]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
End:
    pop     {r4, r5, pc}