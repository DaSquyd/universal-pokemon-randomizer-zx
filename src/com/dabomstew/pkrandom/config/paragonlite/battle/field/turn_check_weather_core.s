    ldrh    r2, [r0, #BattleField.weather]
    cmp     r2, #WEATHER_None
    beq     ReturnNone
    
    ldrh    r1, [r0, #BattleField.weatherTurns]
    cmp     r1, #0xFF
    beq     ReturnNone
    
    ; Decrement
    sub     r1, #1
    strh    r1, [r0, #BattleField.weatherTurns]
    bne     ReturnNone
    
    mov     r1, #WEATHER_None
    strh    r1, [r0, #BattleField.weather]
    
    mov     r0, r2
    bx      lr
    
ReturnNone:
    mov     r0, #WEATHER_None
    bx      lr