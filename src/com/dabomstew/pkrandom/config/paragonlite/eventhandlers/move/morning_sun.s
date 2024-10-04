    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_GetEffectiveWeather
    cmp     r0, #WEATHER_None
    beq     None
    cmp     r0, #WEATHER_Sun
    beq     Sun
    
    mov     r1, #4
    lsl     r1, #8 ; 1024 (25%)
    b       RewriteValue
    
None:
    mov     r1, #4
    lsl     r1, #9 ; 2048 (50%)
    b       RewriteValue
    
Sun:
    ldr     r1, =2732 ; 66%
    
RewriteValue:
    mov     r0, #VAR_Ratio
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4-r6, pc}
    