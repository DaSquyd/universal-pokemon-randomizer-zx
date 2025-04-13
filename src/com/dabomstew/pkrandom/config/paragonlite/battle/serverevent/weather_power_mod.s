    cmp     r0, #WEATHER_Sun
    beq     Sun
    
    cmp     r0, #WEATHER_Rain
    beq     Rain
    
ReturnOne:
    ldr     r0, =0x1000
    bx      lr
    
Sun:
    cmp     r1, #TYPE_Fire
    beq     ReturnStrong
    
    cmp     r1, #TYPE_Water
    bne     ReturnOne
    
ReturnWeak:
    ldr     r0, =(WEATHER_MOD_WEAK * 0x1000)
    bx      lr
    
Rain:
    cmp     r1, #TYPE_Fire
    beq     ReturnWeak
    
    cmp     r1, #TYPE_Water
    bne     ReturnOne
    
ReturnStrong:
    ldr     r0, =(WEATHER_MOD_STRONG * 0x1000)
    bx      lr
    