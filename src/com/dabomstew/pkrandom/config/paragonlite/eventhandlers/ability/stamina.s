    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_SubstituteFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    
    mov     r1, #2
    mov     r2, #1
    mov     r7, r0
    mov     r6, #0
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    beq     Return
    
    mov     r0, r7
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     Return
    
; Apply boost
    mov     r0, r5
    mov     r1, #2
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #14
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    mov     r6, #1
    strb    r6, [r1, #15]
    strb    r4, [r1, #16]
    strb    r6, [r1, #14]
    mov     r0, #2 ; Defense stat
    str     r0, [r1, #4]
    mov     r0, #1 ; Boost amount
    strb    r0, [r1, #12]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #3
    mov     r2, r4
    bl      Battle::Handler_PushRun

Return:
    pop     {r3-r7, pc}