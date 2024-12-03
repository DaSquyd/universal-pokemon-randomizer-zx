    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::CommonIsFlowerGiftPoke
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, r5
    bl      Battle::Handler_GetWeather
    cmp     r0, #WEATHER_Sun
    bne     Return
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    mov     r1, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #CAT_Special
    bne     Return
    
    mov     r0, #VAR_Ratio
    mov     r1, #(0x1800 >> 10) ; 1.5x
    lsl     r1, #10
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r3-r5, pc}