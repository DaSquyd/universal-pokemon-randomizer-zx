    push    {r3-r5, lr}
    mov     r5, r0
    mov     r4, r1
    
    bl      Battle::EventVar_Push
    mov     r0, #VAR_Terrain
    mov     r1, r4
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_MoveFailFlag
    mov     r1, #FALSE
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, r5
    mov     r1, #EVENT_OnTerrainChange
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_MoveFailFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, r5
    mov     r1, #EVENT_OnPostTerrainChange
    bl      Battle::Event_CallHandlers
    
Return:
    bl      Battle::EventVar_Pop
    pop     {r3-r5, pc}
    