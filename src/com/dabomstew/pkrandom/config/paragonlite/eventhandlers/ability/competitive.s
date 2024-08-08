    push    {r3-r7, lr}
    mov     r0, #2
    mov     r5, r1
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #3
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r1, r0, #24
    mov     r0, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #0
    bne     End
    
    mov     r0, #32
    mov     r7, #32
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bge     End
    
    mov     r0, r5
    mov     r1, #14
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    ldr     r2, [r1, #0]
    lsl     r0, r7, #18
    orr     r0, r2
    str     r0, [r1, #0x00]
    mov     r0, #3 ; sp. atk
    str     r0, [r1, #0x04]
    mov     r0, #2 ; boost amount
    strb    r0, [r1, #0x0C]
    mov     r0, #1
    strb    r0, [r1, #0x0E]
    strb    r0, [r1, #0x0F]
    mov     r0, r5
    strb    r4, [r1, #0x10]
    bl      Battle::Handler_PopWork
    
End:
    pop     {r3-r7, pc}