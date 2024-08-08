    push    {r4, lr}
    add     sp, #-8
    mov     r4, #9
    str     r4, [sp, #0]
    mov     r4, #0
    str     r4, [sp, #4]
    bl      Battle::CommonBerrySuperEffectiveCheck
    add     sp, #8
    pop     {r4, pc}