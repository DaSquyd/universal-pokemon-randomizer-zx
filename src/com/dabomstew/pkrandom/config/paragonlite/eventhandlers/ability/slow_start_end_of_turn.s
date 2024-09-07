    push    {r3-r7, lr}
    mov     r6, r1
    mov     r5, r2
    mov     r4, r3
    
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    ldr     r0, [r4, #8] ; finished slow start
    cmp     r0, #0
    bne     Return
    
    ldr     r0, [r4, #4] ; turns active with slow start
    cmp     r0, #3 ; 3 turns
    bge     GetActTogether
    
    mov     r0, r6
    mov     r1, r5
    mov     r2, sp
    bl      Battle::Handler_GetThisTurnAction
    cmp     r0, #0
    beq     Return
    
    ldr     r0, [sp, #0x00]
    lsl     r0, #28
    lsr     r0, #28
    cmp     r0, #3
    beq     Return
    
    ldr     r0, [r4, #4]
    add     r0, #1
    str     r0, [r4, #4] ; increment slow start counter
    
GetActTogether:
    ldr     r0, [r4, #4]
    cmp     r0, #3 ; total turns
    blt     Return
    
    mov     r0, r6
    mov     r1, #4
    mov     r2, r5
    mov     r2, r5
    bl      Battle::Handler_PushWork
    
    mov     r7, r0
    ldr     r2, =499 ; "[user] finally got its act together!"
    add     r0, r7, #4
    mov     r1, #2 ; File 0x12
    bl      Battle::Handler_StrSetup
    
    add     r0, r7, #4
    mov     r1, r5
    bl      Battle::Handler_AddArg
    
    mov     r0, r6
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
    mov     r0, #0
    str     r0, [r4, #0x0C] ; (false) under effect of slow start
    mov     r0, #1
    str     r0, [r4, #0x08] ; (true) got act together
    
Return:
    pop     {r3-r7, pc}