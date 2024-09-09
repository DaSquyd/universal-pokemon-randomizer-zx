    push    {r4-r5, lr}
    mov     r0, #2
    mov     r5, r1
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    mov     r0, r5
    bl      Battle::ServerEvent_GetWeather
    cmp     r0, #3 ; Hail
    bne     Return
    mov     r0, #0x35
    mov     r1, #4
    lsl     r1, #11 ; 8192 (2x)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r5, pc}
    
    
    
    