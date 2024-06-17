    push    {r4, lr}
    mov     r0, r1
    mov     r1, r2
    mov     r2, #3 ; freeze(4) - 1
    mov     r4, r3
    bl      Battle::TryRemoveStatus
    str     r0, [r4, #0] ; result
    pop     {r4, pc}
    