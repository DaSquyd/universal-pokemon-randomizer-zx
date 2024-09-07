    mov     r1, #0
    cmp     r0, #18
    bcc     TryClients1And2
    add     r1, #6
    
TryClients1And2:
    cmp     r0, #6
    bcc     ApplyShift
    add     r1, #6
    
ApplyShift:
    sub     r0, r1  
    bx      lr