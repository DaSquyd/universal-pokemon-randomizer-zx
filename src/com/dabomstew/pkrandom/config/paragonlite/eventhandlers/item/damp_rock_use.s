    push    {r3, lr}
    mov     r3, #WEATHER_Rain
    bl      Battle::CommonWeatherChangeItemUse
    pop     {r3, pc}