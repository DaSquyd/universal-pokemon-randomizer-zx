    cmp     r0, #WEATHER_Sun
    beq     Sun
    
    cmp     r0, #WEATHER_Rain
    beq     Rain
    
    b       ReturnOne
    
Sun:
    cmp     r1, #TYPE_Fire
    bne     Sun_CheckWater
    mov     r0, #5
    b       ShiftAndReturn
    
Sun_CheckWater:
    cmp     r1, #TYPE_Water
    bne     ReturnOne
    mov     r0, #3
    b       ShiftAndReturn
    
Rain:
    cmp     r1, #TYPE_Fire
    bne     Rain_CheckWater
    mov     r0, #3
    b       ShiftAndReturn
    
Rain_CheckWater:
    cmp     r1, #TYPE_Water
    bne     ReturnOne
    mov     r0, #5
    b       ShiftAndReturn
    
ReturnOne:
    mov     r0, #4
    
ShiftAndReturn:
    lsl     r0, #10
    bx      lr