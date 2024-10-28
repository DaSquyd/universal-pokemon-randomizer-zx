    push    {r4-r6, lr}
    mov     r6, r0 ; type_pair_1
    mov     r5, r1 ; type_pair_2
    
    bl      Battle::TypePair_GetType1
    cmp     r0, #TYPE_Null
    beq     CheckType2
    
    mov     r1, r0
    mov     r0, r5
    bl      Battle::TypePair_HasType
    cmp     r0, #FALSE
    bne     ReturnTrue
    
CheckType2:
    mov     r0, r6
    bl      Battle::TypePair_GetType2
    cmp     r0, #TYPE_Null
    beq     CheckType3
    
    mov     r1, r0
    mov     r0, r5
    bl      Battle::TypePair_HasType
    cmp     r0, #FALSE
    bne     ReturnTrue
    
CheckType3:
    mov     r0, r6
    bl      Battle::TypePair_GetType3
    cmp     r0, #TYPE_Null
    beq     ReturnFalse
    
    mov     r1, r0,
    mov     r0, r5
    bl      Battle::TypePair_HasType
    cmp     r0, #FALSE
    bne     ReturnTrue
    
ReturnFalse:
    mov     r0, #FALSE
    pop     {r4-r6, pc}
    
ReturnTrue:
    mov     r0, #TRUE
    pop     {r4-r6, pc}