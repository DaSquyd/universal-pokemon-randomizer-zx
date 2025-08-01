    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    bl      Battle::GetPoke
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    cmp     r0, #125 ; Sheer Force
    beq     Return
    
    mov     r0, #2
    bl      Battle::Random
    cmp     r0, #0
    beq     Return
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    mov     r0, #STSG_Defense
    str     r0, [r1, #HandlerParam_ChangeStatStage.stat]
    mov     r0, #2
    strb    r0, [r1, #HandlerParam_ChangeStatStage.amount]
    mov     r0, #1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    strb    r4, [r1, #HandlerParam_ChangeStatStage.pokeId]
    
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r5, pc}