; r0: serverFlow
; r1: battlePoke

    push    {r3-r6, lr}
    mov     r5, r0
    mov     r4, r1
    
    mov     r0, r4
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    mov     r6, r0
    
CheckSunSoaked:
    ldr     r1, =511 ; Sun-Soaked
    cmp     r6, r1
    bne     GetWeather
    mov     r0, #WEATHER_Sun
    b       Return
    
GetWeather:
    mov     r0, r5
    bl      Battle::GetWeather
    
Return:
    pop     {r3-r6, pc}