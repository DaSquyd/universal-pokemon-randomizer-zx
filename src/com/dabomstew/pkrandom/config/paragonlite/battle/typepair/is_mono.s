    bl      Battle::TypePair_GetCount
    cmp     r0, #1
    beq     ReturnTrue
    
ReturnFalse:
    mov     r0, #FALSE
    bx      lr
    
ReturnTrue:
    mov     r0, #TRUE
    bx      lr