    push    {r4-r6, lr}
    mov     r5, r0 ; ServerFlow
    mov     r4, r1 ; weather
    mov     r6, r2 ; turns
    
    mov     r0, r4
    mov     r1, r6
    bl      Battle::Field_SetTerrain
    
    ldr     r0, [r5, #ServerFlow.serverCommandQueue]
    mov     r1, #SCMD_WeatherStart
    mov     r2, r4
    mov     r3, r6
    bl      Battle::ServerDisplay_AddCommon
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::ServerControl_PostChangeTerrain
    pop     {r4-r6, pc}