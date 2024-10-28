    push    {r3-r5, lr}
    mov     r5, r0 ; typePair
    mov     r4, r1 ; queryType
    
    bl      Battle::TypePair_GetType1
    cmp     r4, r0
    beq     ReturnTrue
    
    mov     r0, r5
    bl      Battle::TypePair_GetType2
    cmp     r4, r0
    beq     ReturnTrue
    
    mov     r0, r5
    bl      Battle::TypePair_GetType3
    cmp     r4, r0
    beq     ReturnTrue
    
ReturnFalse:
    mov     r0, #FALSE
    pop     {r3-r5, pc}
    
ReturnTrue:
    mov     r0, #TRUE
    pop     {r3-r5, pc}