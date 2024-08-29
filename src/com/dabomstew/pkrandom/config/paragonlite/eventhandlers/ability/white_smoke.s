    push    {r3-r6, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
; Return if the ability owner is not the stat change target
    mov     r0, #0x02
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
; Return if positive stat change
    mov     r0, #0x20
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bge     Return
    
    mov     r0, #0x41
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    str     r0, [r6]
    
Return:
    pop     {r3-r6, pc}