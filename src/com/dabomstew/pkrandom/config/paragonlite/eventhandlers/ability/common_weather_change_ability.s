    push    {r3-r7, lr}
    mov     r5, r0
    mov     r4, r1
    mov     r6, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, #HE_ChangeWeather
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    ldr     r2, [r7, #HandlerParam_ChangeWeather.header]
    mov     r0, #(BHP_AbilityPopup >> 23)
    lsl     r0, #23
    orr     r0, r2
    str     r0, [r7, #HandlerParam_ChangeWeather.header]
    strb    r6, [r7, #HandlerParam_ChangeWeather.weather]
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r2, r0
    mov     r0, r5
    mov     r1, r6
    bl      Battle::ServerEvent_IncreaseMoveWeatherTurns
    add     r0, #5
    strb    r0, [r7, #HandlerParam_ChangeWeather.turns]
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r7, pc}