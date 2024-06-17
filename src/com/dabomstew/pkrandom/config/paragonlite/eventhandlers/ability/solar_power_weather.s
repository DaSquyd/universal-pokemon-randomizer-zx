; 1/8 -> 1/10
    push    {r3-r7, lr}
    mov     r0, #2
    mov     r5, r1
    mov     r4, r2
    mov     r7, #2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #57 ; weather
    bl      Battle::EventVar_GetValue
    cmp     r0, #1 ; sun
    bne     End
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r1, #10 ; 1/10 hp
    bl      Battle::DivideMaxHPZeroCheck
    
    mov     r6, r0
    mov     r0, r5
    mov     r1, r7
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #7
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    strb    r4, [r1, #6]
    mov     r0, r5
    strh    r6, [r1, #4]
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #3
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
End:
    pop     {r3-r7, pc}
