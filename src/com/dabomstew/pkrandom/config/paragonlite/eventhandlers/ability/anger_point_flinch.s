    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #34
    bl      Battle::EventVar_GetValue
    cmp     r0, #6
    bne     End
    
    mov     r0, #2
    mov     r6, #2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, r5
    mov     r1, #14
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    ldr     r2, [r1, #0]
    lsl     r0, r6, #22
    orr     r0, r2
    str     r0, [r1, #0]
    mov     r0, #1 ; attack stat and boost amount
    str     r0, [r1, #4]
    mov     r2, #1 ; stat boost amount
    strb    r2, [r1, #12]
    mov     r0, #0
    strb    r0, [r1, #14]
    strb    r2, [r1, #15]
    mov     r0, r5
    strb    r4, [r1, #16]
    bl      Battle::Handler_PopWork
    
End:
    pop     {r4-r6, pc}
    