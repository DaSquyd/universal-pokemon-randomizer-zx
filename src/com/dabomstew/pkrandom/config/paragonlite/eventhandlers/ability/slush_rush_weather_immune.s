    push    {r3, r4, lr}
    add     sp, #-4
    mov     r4, #3
    str     r4, [sp]
    bl      Battle::CommonWeatherGuard
    add     sp, #4
    pop     {r3, r4, pc}
