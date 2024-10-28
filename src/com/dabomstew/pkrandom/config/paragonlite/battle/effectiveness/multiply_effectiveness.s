    cmp     r0, #0
    beq     ReturnZero
    
    cmp     r1, #0
    beq     ReturnZero

    add     r0, r1
    sub     r0, #EFF_Neutral
    
    cmp     r0, #EFF_Null
    bcs     ReturnZero
    bx      lr
    
ReturnZero:
    mov     r0, #EFF_Zero
    bx      lr