    push    {r3-r7, lr}
    mov     r5, r0 ; ServerFlow*
    mov     r4, r1 ; HandlerParam_ChangeTerrain
    
    ldr     r0, [r5, #ServerFlow.pokeCon]
    ldr     r1, [r4, #HandlerParam_ChangeTerrain.header]
    lsl     r1, r1, #19
    lsr     r1, r1, #27
    bl      Battle::PokeCon_GetPoke
    mov     r7, r0
    
    mov     r6, #FALSE
    
    ldrb    r1, [r4, #HandlerParam_ChangeTerrain.terrain]
    cmp     r1, #TERRAIN_None
    beq     Return
    
    mov     r0, r5
    ldrb    r2, [r4, #HandlerParam_ChangeTerrain.turns]
    bl      Battle::ServerControl_ChangeTerrainCheck
    cmp     r0, #0
    beq     Return
    
    ldr     r0, [r4, #HandlerParam_ChangeTerrain.header]
    lsl     r0, #8
    lsr     r0, #31 ; BHP_AbilityPopup
    beq     ChangeTerrainCore
    

AddAbilityPopup:
    mov     r0, r5
    mov     r1, r7
    bl      Battle::ServerDisplay_AbilityPopupAdd
    

ChangeTerrainCore:
    ldrb    r1, [r4, #HandlerParam_ChangeTerrain.terrain]
    ldrb    r2, [r4, #HandlerParam_ChangeTerrain.turns]
    mov     r0, r5
    bl      Battle::ServerControl_ChangeTerrainCore
    
    mov     r0, r5
    mov     r1, r4
    add     r1, #HandlerParam_ChangeTerrain.exStr
    bl      Battle::Handler_SetString
    
    mov     r6, #TRUE
    
    ; Check ability popup
    ldr     r0, [r4, #HandlerParam_ChangeTerrain.header]
    lsl     r0, r0, #8
    lsr     r0, r0, #31 ; BHP_AbilityPopup
    beq     Return
        

RemoveAbilityPopup:
    mov     r0, r5
    mov     r1, r7
    bl      Battle::ServerDisplay_AbilityPopupRemove

Return:
    lsl     r0, r6, #24
    lsr     r0, r0, #24
    pop     {r3-r7, pc}