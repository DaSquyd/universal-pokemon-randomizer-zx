    push    {r4-r6, lr}
    mov     r5, r0
    mov     r6, r1 ; weather
    
    mov     r4, #0
    
LoopStart:
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Field_ClearFactorWork
    
    add     r4, #1
    cmp     r4, #8
    bcc     LoopStart
    
    strh    r6, [r5, #BattleField.weather]
    
    ; TODO: use r2 arg for this
    mov     r0, #0x00
    strh    r0, [r5, #BattleField.terrain]
    
    mov     r0, #0xFF
    strh    r0, [r5, #BattleField.weatherTurns]
    strh    r0, [r5, #BattleField.terrainTurns]
    
    pop     {r4-r6, pc}