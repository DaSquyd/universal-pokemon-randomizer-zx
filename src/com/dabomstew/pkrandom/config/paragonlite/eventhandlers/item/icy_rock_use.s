    push    {r3, lr}
    mov     r3, #WEATHER_Hail
    bl      Battle::CommonWeatherChangeItemUse
    pop     {r3, pc}