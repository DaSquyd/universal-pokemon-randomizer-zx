    mov     r0, r2
    mov     r1, #TYPE_Fighting
    ldr     r3, =(Battle::CommonMoveTypeChange_Power+1)
    bx      r3
