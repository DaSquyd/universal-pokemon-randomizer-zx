    ldrh    r2, [r0, #BattleField.terrain]
    cmp     r2, #TERRAIN_None
    beq     ReturnNone
    
    ldrh    r1, [r0, #BattleField.terrainTurns]
    cmp     r1, #0xFF
    beq     ReturnNone
    
    ; Decrement
    sub     r1, #1
    strh    r1, [r0, #BattleField.terrainTurns]
    bne     ReturnNone
    
    mov     r1, #TERRAIN_None
    strh    r1, [r0, #BattleField.terrain]
    
    mov     r0, r2
    bx      lr
    
ReturnNone:
    mov     r0, #TERRAIN_None
    bx      lr