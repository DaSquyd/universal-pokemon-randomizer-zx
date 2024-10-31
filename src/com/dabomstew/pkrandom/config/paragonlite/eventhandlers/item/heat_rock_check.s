    push    {r3, lr}
    mov     r3, #WEATHER_Sun
    bl      Battle::CommonWeatherChangeItemCheck
    pop     {r3, pc}