    push    {r3-r7, lr}
    mov     r5, r0
    mov     r4, r1
    
    ldr     r7, =ServerFlow.heManager
    add     r0, r5, r7
    bl      Battle::HEManager_PushState
    mov     r6, r0
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::ServerEvent_PostChangeTerrain
    
    add     r0, r5, r7
    mov     r1, r6
    bl      Battle::HEManager_PopState
    pop     {r3-r7, pc}