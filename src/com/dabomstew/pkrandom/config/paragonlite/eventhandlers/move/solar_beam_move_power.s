    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_GetEffectiveWeather
    sub     r0, #2
    cmp     r0, #2
    bhi     Return
    
    mov     r0, #VAR_MovePower
    mov     r1, #4
    lsl     r1, #9 ; 2048 (0.5x)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r3-r5, pc}