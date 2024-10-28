    push    {r3-r5, lr}
    mov     r5, r0
    
    mov     r4, #0
    
CheckType1:
    bl      Battle::TypePair_GetType1
    cmp     r0, #TYPE_Null
    beq     CheckType2
    
    add     r4, #1
    
CheckType2:
    mov     r0, r5
    bl      Battle::TypePair_GetType2
    cmp     r0, #TYPE_Null
    beq     CheckType3
    
    add     r4, #1
    
CheckType3:
    mov     r0, r5
    bl      Battle::TypePair_GetType3
    cmp     r0, #TYPE_Null
    beq     Return
    
    add     r4, #1
    
Return:
    mov     r0, r4
    pop     {r3-r5, pc}