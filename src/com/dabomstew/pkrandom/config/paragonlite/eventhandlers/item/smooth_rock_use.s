    push    {r3, lr}
    mov     r3, #WEATHER_Sand
    bl      Battle::CommonWeatherChangeItemUse
    pop     {r3, pc}