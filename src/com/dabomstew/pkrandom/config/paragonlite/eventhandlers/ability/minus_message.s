    push    {r3, lr}
    mov     r0, r1
    mov     r1, r2
    ldr     r2, =BTLTXT_Minus_Activate
    bl      Battle::CommonUserMessage
    pop     {r3, pc}