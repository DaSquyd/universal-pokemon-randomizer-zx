    lsl     r0, #27
    lsr     r0, #27
    
    lsl     r1, #27
    lsr     r1, #22
    
    lsl     r2, #27
    lsr     r2, #17
    
    orr     r0, r1
    orr     r0, r2
    bx      lr