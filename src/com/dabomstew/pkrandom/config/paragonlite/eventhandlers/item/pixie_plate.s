    push    {r3, lr}
    mov     r3, #TYPE_Fairy
    bl      Battle::CommonTypeBoostItem
    pop     {r3, pc}