    push    {r4-r7, lr}
    mov     r7, r0 ; type_pair
    mov     r6, r1 ; type_1
    mov     r5, r2 ; type_2
    mov     r4, r3 ; type_3
    
    bl      Battle::TypePair_GetType1
    strb    r0, [r6]
    
    bl      Battle::TypePair_GetType2
    strb    r0, [r5]
    
    bl      Battle::TypePair_GetType3
    strb    r0, [r4]
    
    pop     {r4-r7, pc}