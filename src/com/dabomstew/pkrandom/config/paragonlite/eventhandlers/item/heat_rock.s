    push    {r3, lr}
    mov     r3, #WEATHER_Sun
    bl      Battle::CommonWeatherChangeItem
    pop     {r3, pc}