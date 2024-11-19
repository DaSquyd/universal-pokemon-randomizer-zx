    mov     r1, #0
    strh    r1, [r0, #BattleField.terrain]
    strh    r1, [r0, #BattleField.terrainTurns]
    bx      lr