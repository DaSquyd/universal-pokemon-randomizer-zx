    mov     r0, r2
    mov     r1, #TYPE_Fire
    ldr     r3, =(Battle::CommonMoveTypeChange_Power+1)
    bx      r3
