    push    {r3, lr}
    mov     r3, #0 ; Normal-type
    bl      Battle::CommonTypeBoostItem
    pop     {r3, pc}