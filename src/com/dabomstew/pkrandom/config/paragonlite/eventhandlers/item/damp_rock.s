    push    {r3, lr}
    mov     r3, #WEATHER_Rain
    bl      Battle::CommonWeatherChangeItem
    pop     {r3, pc}