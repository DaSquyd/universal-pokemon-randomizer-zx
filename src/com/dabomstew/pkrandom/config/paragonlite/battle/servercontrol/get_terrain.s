    push    {r4, lr}
    mov     r4, r0 ; ServerFlow
    
    bl      Battle::EventVar_Push
    
    mov     r0, #VAR_MoveFailFlag
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, r4
    mov     r1, #EVENT_OnTerrainCheck
    bl      Battle::Event_CallHandlers
    
    
    mov     r0, #VAR_MoveFailFlag
    bl      Battle::EventVar_GetValue
    mov     r4, r0
    
    bl      Battle::EventVar_Pop
    
    mov     r0, #TERRAIN_None
    cmp     r4, #FALSE
    bne     Return
    
    bl      Battle::Field_GetTerrain
    
Return:
    pop     {r4, pc}