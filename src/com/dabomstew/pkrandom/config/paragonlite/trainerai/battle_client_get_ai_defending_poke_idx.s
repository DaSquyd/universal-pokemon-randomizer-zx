LoopStart:
    cmp     r0, #6
    bcc     Return
    
    sub     r0, #6
    b       LoopStart
    
Return:
    bx      lr