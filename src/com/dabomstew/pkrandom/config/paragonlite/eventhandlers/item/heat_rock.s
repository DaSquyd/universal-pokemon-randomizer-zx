    ldr     r3, =(Battle::CommonWeatherChangeItem+1)
    mov     r0, r1
    mov     r1, r2
    mov     r2, #1 ; Sun
    bx      r3