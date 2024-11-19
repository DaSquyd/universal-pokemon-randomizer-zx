#define S_Heal 0x00

    push    {r3-r6, lr}
    mov     r6, r0 ; ServerFlow
    mov     r4, r1 ; BattlePoke
    mov     r5, r2 ; Terrain
    str     r3, [sp, #S_Heal]
    
    bl      Battle::EventVar_Push
    
    mov     r0, r4
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_Terrain
    mov     r1, r5
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_MoveFailFlag
    mov     r1, #FALSE
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, #VAR_Health
    ldr     r1, [sp, #S_Heal]
    bl      Battle::EventVar_SetValue
    
    mov     r0, r6
    mov     r1, #EVENT_OnTerrainHealReaction
    bl      Battle::Event_CallHandlers
    
    mov     r0, #VAR_Health
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    
    mov     r0, #VAR_MoveFailFlag
    bl      Battle::EventVar_GetValue
    mov     r4, r0
    
    bl      Battle::EventVar_Pop
    
    mov     r0, #0
    cmp     r4, #FALSE
    bne     Return
    
    mov     r0, r6
    
Return:
    pop     {r3-r6, pc}