    ldrh    r1, [r0, #BattleField.terrain]
    cmp     r1, #TERRAIN_None
    beq     ReturnZero
    
    ldrh    r0, [r0, #BattleField.terrainTurns]
    bx      lr
    
ReturnZero:
    mov     r0, #0
    bx      lr