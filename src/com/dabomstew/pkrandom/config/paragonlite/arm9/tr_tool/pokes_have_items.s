    
    ldrb    r0, [r0, #TrainerData.flags]
    mov     r1, #(1 << TrainerData_Flags.hasItemBit)
    tst     r0, r1
    beq     Return ; defaults to FALSE
    
    mov     r0, #TRUE
    
Return:
    bx      lr