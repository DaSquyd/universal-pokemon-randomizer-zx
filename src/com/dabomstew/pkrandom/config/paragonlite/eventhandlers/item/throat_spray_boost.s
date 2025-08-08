    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    mov     r0, #STSG_SpAtk
    str     r0, [r1, #0x04]
    mov     r0, #1 ; boost amount
    strb    r0, [r1, #0x0C]
    mov     r0, #1
    strb    r0, [r1, #0x0E]
    strb    r0, [r1, #0x0F]
    strb    r4, [r1, #0x10]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0

End:
    pop     {r3-r5, pc}