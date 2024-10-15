    push    {r3, lr}
    mov     r3, #WEATHER_Sand
    bl      Battle::CommonWeatherChangeItem
    pop     {r3, pc}