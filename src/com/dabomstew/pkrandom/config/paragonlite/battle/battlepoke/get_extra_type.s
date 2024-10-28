    mov     r1, #0xFB
    ldrb    r0, [r0, r1]
    lsr     r1, r0, #7
    cmp     r1, #0
    beq     ReturnNull
    
Return:
    mov     r1, #0x1F
    and     r0, r1
    bx      lr
    
ReturnNull:
    mov     r0, #TYPE_Null
    bx      lr