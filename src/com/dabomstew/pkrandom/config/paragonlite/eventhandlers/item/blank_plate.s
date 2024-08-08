    push    {r3, lr}
    mov     r3, #0 ; Normal-type
    bl      Battle::CommonPlateTypeBoost
    pop     {r3, pc}