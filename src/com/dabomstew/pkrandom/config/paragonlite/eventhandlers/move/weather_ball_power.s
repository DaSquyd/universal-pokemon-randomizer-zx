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
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_MoveBasePower
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    lsl     r1, #1
    mov     r0, #VAR_MoveBasePower
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r3-r5, pc}