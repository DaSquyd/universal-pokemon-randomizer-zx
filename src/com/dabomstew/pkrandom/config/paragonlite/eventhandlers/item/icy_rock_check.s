    push    {r3, lr}
    mov     r3, #WEATHER_Hail
    bl      Battle::CommonWeatherChangeItemCheck
    pop     {r3, pc}