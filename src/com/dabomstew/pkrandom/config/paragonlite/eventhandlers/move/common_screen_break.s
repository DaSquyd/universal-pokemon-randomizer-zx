    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    bl      Battle::GetTeamIdFromPokePos
    mov     r6, r0
    mov     r1, #SC_Reflect
    bl      BattleServer::SideStatus_IsActive
    cmp     r0, #FALSE
    bne     Work
    
    mov     r0, r6
    mov     r1, #SC_LightScreen
    bl      BattleServer::SideStatus_IsActive
    cmp     r0, #FALSE
    bne     Work
    
    mov     r0, r6
    mov     r1, #SC_AuroraVeil
    bl      BattleServer::SideStatus_IsActive
    cmp     r0, #FALSE
    beq     Return
    
Work:
    mov     r0, r5
    mov     r1, #HE_RemoveSideCondition
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    mov     r0, #3 ; size of flags section
    strb    r0, [r1, #(HandlerParam_RemoveSideCondition.flags + 0)]
    
    mov     r0, #((1 << SC_Reflect) | (1 << SC_LightScreen))
    strb    r0, [r1, #(HandlerParam_RemoveSideCondition.flags + 1)]
    
    mov     r0, #((1 << SC_AuroraVeil) >> 8)
    strb    r0, [r1, #(HandlerParam_RemoveSideCondition.flags + 2)]
    
    strb    r6, [r1, #HandlerParam_RemoveSideCondition.side]
    
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
    ; anim switch
    mov     r0, r5
    mov     r1, #HE_SetAnimationId
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    mov     r0, #1 ; anim index
    strb    r0, [r1, #HandlerParam_SetAnimation.id]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r6, pc}
    