    mov     r0, r2
    mov     r1, #TYPE_Fire
    ldr     r3, =(Battle::CommonMoveTypeChange_Type+1)
    bx      r3
