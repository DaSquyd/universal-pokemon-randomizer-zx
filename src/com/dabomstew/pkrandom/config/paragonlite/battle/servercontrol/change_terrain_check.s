    push    {r3-r5, lr}
    mov     r5, r1 ; weather
    mov     r4, r2 ; turns
    
    cmp     r5, #WEATHER_NULL
    bcs     ReturnFalse
    
    bl      Battle::Field_GetTerrain
    cmp     r5, r0
    bne     ReturnTrue
    
    cmp     r4, #WEATHER_INDEFINITE
    bne     ReturnFalse
    
    bl      Battle::Field_GetTerrainTurns
    cmp     r0, #WEATHER_INDEFINITE
    bne     ReturnTrue
    
ReturnFalse:
    mov     r0, #FALSE
    pop     {r3-r5, pc}
    
ReturnTrue:
    mov     r0, #TRUE
    pop     {r3-r5, pc}