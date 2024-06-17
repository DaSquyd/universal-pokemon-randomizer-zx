; 0x5B
    push    {r3, lr}
    mov     r0, r1
    mov     r1, r2
    mov     r2, r3
    mov     r3, #5 ; Speed
    bl      Battle::CommonStatDropGuardCheck
    pop     {r3, pc}