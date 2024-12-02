    push    {r4-r6, lr}
    mov     r6, r0
    mov     r5, r1
    mov     r4, r2
    mov     r7, r3
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ; activate once; mostly necessary for rotation battles
    ldrb    r0, [r7]
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #TRUE
    strb    r0, [r7]
    
PushRun:
    mov     r0, r6
    mov     r1, r5
    mov     r2, r4
    bl      Battle::ItemEvent_PushRun
    
Return:
    pop     {r4-r6, pc}