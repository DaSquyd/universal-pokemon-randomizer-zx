    push    {r4-r6, lr}
    mov     r0, #2
    mov     r5, r1
    mov     r4, r2
    mov     r6, #2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::GetPokeStatus
    cmp     r0, #0
    beq     Return
    
    mov     r0, r5
    mov     r1, #0x0B
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    ldr     r2, [r1]
    lsl     r0, r6, #22
    orr     r2, r0
    lsl     r0, r6, #24
    orr     r0, r2
    str     r0, [r1]
    mov     r0, #36
    str     r0, [r1, #4]
    strb    r4, [r1, #8]
    mov     r0, #1
    strb    r0, [r1, #20]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r6, pc}