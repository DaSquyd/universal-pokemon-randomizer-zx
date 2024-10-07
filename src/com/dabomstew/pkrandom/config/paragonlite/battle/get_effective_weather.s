; r0: serverFlow
; r1: battlePoke

    push    {r3-r6, lr}
    mov     r5, r0
    mov     r4, r1
    
    mov     r0, r4
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    mov     r6, r0
    
CheckDamp:
    mov     r1, #6 ; Damp
    cmp     r6, r1
    bne     CheckSunSoaked
    mov     r0, #WEATHER_Rain
    b       Return
    
CheckSunSoaked:
    ldr     r1, =511 ; Sun-Soaked
    cmp     r6, r1
    bne     GetWeather
    mov     r0, #WEATHER_Sun
    b       Return
    
GetWeather:
    mov     r0, r5
    bl      Battle::ServerEvent_GetWeather
    
Return:
    pop     {r3-r6, pc}