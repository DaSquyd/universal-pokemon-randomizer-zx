    push    {r3-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_GetEffectiveWeather
    mov     r6, r0
    
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    
    mov     r4, #0 ; animIdx
    cmp     r6, #WEATHER_Sand
    bhi     RewriteValue
    
    #SWITCH r0, r6
    #CASE RewriteValue
    #CASE Sun
    #CASE Rain
    #CASE Hail
    #CASE Sand
    
Sun:
    mov     r1, #TYPE_Fire
    mov     r4, #1 ; animIdx
    b       RewriteValue
    
Rain:
    mov     r1, #TYPE_Water
    mov     r4, #4 ; animIdx
    b       RewriteValue
    
Hail:
    mov     r1, #TYPE_Ice
    mov     r4, #2 ; animIdx
    b       RewriteValue
    
Sand:
    mov     r1, #TYPE_Rock
    mov     r4, #3 ; animIdx
    
RewriteValue:
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_RewriteType
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_SetMoveAnimationIndex
    
Return:
    pop     {r3-r6, pc}