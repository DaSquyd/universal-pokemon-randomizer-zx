    push    {r4-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    bl      Battle::ServerEvent_GetWeather
    cmp     r0, #WEATHER_Sun
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #CAT_Special
    bne     Return
    
    mov     r0, #VAR_RATIO
    ldr     r1, =(0x1000 * ABILITY_SOLAR_POWER_MULTIPLIER)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r5, pc}
