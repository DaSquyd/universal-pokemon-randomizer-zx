    push    {r3, lr}
    mov     r3, #17 ; Fairy-type
    bl      Battle::CommonPlateTypeBoost
    pop     {r3, pc}