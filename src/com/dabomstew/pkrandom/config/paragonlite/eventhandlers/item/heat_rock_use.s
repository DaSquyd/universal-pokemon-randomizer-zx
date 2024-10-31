    push    {r3, lr}
    mov     r3, #WEATHER_Sun
    bl      Battle::CommonWeatherChangeItemUse
    pop     {r3, pc}