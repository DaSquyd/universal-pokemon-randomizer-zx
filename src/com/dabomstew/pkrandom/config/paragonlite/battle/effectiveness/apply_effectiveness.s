CheckInvalid:
    cmp     r1, #EFF_Null
    bcs     Return

CheckZero:
    cmp     r1, #EFF_Zero
    bne     CheckSuperEffective
    
ReturnZero:
    mov     r0, #0
    bx      lr
    
    
CheckSuperEffective:
    cmp     r1, #EFF_Neutral
    bls     CheckNotVeryEffective
    
SuperEffective:
    sub     r1, #EFF_Neutral
    lsl     r0, r1
    bx      lr
    
    
CheckNotVeryEffective:
    cmp     r1, #EFF_Neutral
    beq     Return

NotVeryEffective:
    mov     r2, #EFF_Neutral
    sub     r1, r2, r1
    lsr     r0, r1
    
Return:
    bx      lr
    
