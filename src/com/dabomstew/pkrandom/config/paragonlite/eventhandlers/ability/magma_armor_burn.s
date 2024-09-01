; 0x4B
    push    {r4, r5, lr}
    add     sp, #-12
    mov     r0, #4 ; burn
    mov     r5, r1
    mov     r4, r2
    bl      Battle::MakeNonVolatileStatus
    mov     r3, r0
    mov     r0, #10 ; 10%
    str     r0, [sp]
    mov     r0, r5
    mov     r1, r4
    mov     r2, #4 ; burn
    bl      Battle::CommonContactStatusAbility
    add     sp, #12
    pop     {r4, r5, pc}
