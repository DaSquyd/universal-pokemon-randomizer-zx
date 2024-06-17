; 0x5C
    push    {r3, LR}
    mov     r0, r1
    mov     r1, r2
    mov     r2, r3
    ldr     r3, =1159; 1159 ; message index in file 18
    bl      Battle::CommonStatDropGuardMessage
    pop     {r3, pc}