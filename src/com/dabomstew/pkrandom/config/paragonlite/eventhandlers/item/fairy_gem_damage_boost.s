    push    {r3, r4, lr}
    add     sp, #-4
    mov     r4, #17 ; Fairy-type
    str     r4, [sp]
    bl      Battle::CommonGemDamageBoost
    add     sp, #4
    pop     {r3, r4, pc}
    