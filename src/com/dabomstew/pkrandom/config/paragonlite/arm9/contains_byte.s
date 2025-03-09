; r0    array ptr
; r1    array num
; r2    search key

    push    {r4-r6, lr}
    mov     r4, r0
    mov     r5, r1
    mov     r6, r2
    
    mov     r0, #FALSE
    cmp     r5, #0
    bne     Loop_Setup
    
    pop     {r4-r6, pc}
    
Loop_Setup:
    mov     r3, #0

Loop_Start:
    ldrb    r1, [r4, r3]
    cmp     r1, r6
    bne     Loop_End
    
    mov     r0, #TRUE
    pop     {r4-r6, pc}

Loop_End:
    add     r3, #1
    cmp     r3, r5
    bcc     Loop_Start
    
    pop     {r4-r6, pc}