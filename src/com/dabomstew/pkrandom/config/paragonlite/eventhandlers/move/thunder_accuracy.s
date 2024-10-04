    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_GetEffectiveWeather
    cmp     r0, #WEATHER_Sun
    bne     Return
    
    mov     r0, #VAR_Accuracy
    mov     r1, #50
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r3-r5, pc}