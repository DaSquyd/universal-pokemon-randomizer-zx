#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_GENERAL_USE_FLAG 0x51
#DEFINE WEATHER_RAIN 2
#DEFINE TRUE 1

    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    bl      Battle::ServerEvent_GetWeather
    cmp     r0, #WEATHER_RAIN
    bne     Return
    
    mov     r0, #VAR_GENERAL_USE_FLAG
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r3-r5, pc}