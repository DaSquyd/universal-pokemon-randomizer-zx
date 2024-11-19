    push    {r3-r4, lr}
    sub     sp, #4
    mov     r3, #WEATHER_Hail
    str     r4, [sp]
    bl      Battle::CommonWeatherGuard
    add     sp, #4
    pop     {r3-r4, pc}