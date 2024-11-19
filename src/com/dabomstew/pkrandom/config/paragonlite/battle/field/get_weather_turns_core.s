    ldrh    r1, [r0, #BattleField.weather]
    cmp     r1, #WEATHER_None
    beq     ReturnZero
    
    ldrh    r0, [r0, #BattleField.weatherTurns]
    bx      lr
    
ReturnZero:
    mov     r0, #0
    bx      lr