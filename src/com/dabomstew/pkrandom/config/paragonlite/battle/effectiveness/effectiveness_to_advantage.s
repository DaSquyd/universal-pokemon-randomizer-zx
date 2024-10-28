    cmp     r0, #EFF_Neutral
    ble     CheckNeutral
    
SuperEffective:
    mov     r0, #ADV_SuperEffective
    bx      lr
    
CheckNeutral:
    bne     CheckNoEffect
    
    mov     r0, #ADV_Neutral
    bx      lr
    
CheckNoEffect:
    cmp     r0, #EFF_Zero
    beq     ReturnNoEffect
    
    mov     r0, #ADV_NotVeryEffective
    bx      lr
    
ReturnNoEffect:
    mov     r0, #ADV_NoEffect
    bx      lr
    