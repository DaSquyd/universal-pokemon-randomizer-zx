    push    {r4, lr}
    mov     r4, r1
    ldr     r0, [r4, #0x38]
    ldrb    r2, [r4, #1]
    add     r1, r4, #4
    lsl     r3, r2, #2
    ldr     r2, [r1, r3]
    add     r0, r2
    str     r0, [r1, r3]
    ldrb    r0, [r4, #1]
    lsl     r2, r0, #2
    ldr     r0, [r1, r2]
    cmp     r0, #0
    bge     End
    mov     r0, #0
    str     r0, [r1, r2]
    
End:
    add     r4, #0xC4
    ldr     r0, [r4]
    pop     {r4, pc}