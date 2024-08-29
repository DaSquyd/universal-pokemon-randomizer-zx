    ldr     r3, =(Battle::CommonWeatherChangeAbility+1)
    mov     r0, r1
    mov     r1, r2
    mov     r2, #0 ; no weather
    bx      r3