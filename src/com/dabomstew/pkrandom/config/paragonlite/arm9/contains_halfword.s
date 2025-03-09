; r0    array ptr
; r1    array num
; r2    search key
; r3    shift

    push    {r4-r6, lr}
    mov     r4, r0
    mov     r5, r1
    mov     r6, r2
    
;    #printf("    contains?")
    #printf("                arr=0x%08X", r4)
    #printf("                num=%d", r5)
    #printf("                search=%d", r6)
    
    mov     r0, #FALSE
    cmp     r5, #0
    bne     Loop_Setup
    
#if DEBUG
    #printf("                arr is empty!")
    mov     r0, #FALSE
#endif
    pop     {r4-r6, pc}
    
Loop_Setup:
    mov     r3, #0

Loop_Start:
    lsl     r1, r3, #1
    ldrh    r1, [r4, r1]
    cmp     r1, r6
    bne     Loop_End
    
    #printf("                contains at index %d!", r3)
    mov     r0, #TRUE
    pop     {r4-r6, pc}

Loop_End:
    add     r3, #1
    cmp     r3, r5
    bcc     Loop_Start
    
#if DEBUG
    #printf("                does NOT contain!")
    mov     r0, #FALSE
#endif
    pop     {r4-r6, pc}