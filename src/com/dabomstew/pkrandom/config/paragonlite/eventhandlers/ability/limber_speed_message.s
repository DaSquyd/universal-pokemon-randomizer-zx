; 0x5C
    push    {r3, LR}
    mov     r0, r1
    mov     r1, r2
    mov     r2, r3
    ldr     r3, =BTLTXT_Common_SpeedNotLowered
    bl      Battle::CommonStatDropGuardMessage
    pop     {r3, pc}