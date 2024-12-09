    push    {r4-r7, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_MoveCategory
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    cmp     r0, #CAT_Status
    beq     Return
    
    mov     r7, #SC_Reflect
    cmp     r0, #CAT_Physical
    beq     CheckActive
    mov     r7, #SC_LightScreen
    
CheckActive:
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    bl      Battle::GetTeamIdFromPokePos
    mov     r1, r7
    bl      BattleServer::SideStatus_IsEffectActive
    cmp     r0, #FALSE
    bne     Return ; don't re-apply screen
    
    mov     r0, r5
    mov     r1, r4
    mov     r2, r6
    bl      BattleServer::CommonScreenEffect
    
Return:
    pop     {r4-r7, pc}