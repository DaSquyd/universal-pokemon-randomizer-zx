    mov     r1, #0
    strh    r1, [r0, #BattleField.weather]
    strh    r1, [r0, #BattleField.weatherTurns]
    bx      lr