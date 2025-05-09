    push    {r4-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_SubstituteFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_Fire
    bne     Return
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    ldr     r2, [r1, #HandlerParam_ChangeStatStage.header]
    mov     r0, #(BHP_AbilityPopup >> 16)
    lsl     r0, #16
    orr     r0, r2
    str     r0, [r1, #HandlerParam_ChangeStatStage.header]
    
    mov     r0, #STSG_Attack ; 1
    str     r0, [r1, #HandlerParam_ChangeStatStage.stat]
    
    ; mov   r0, #1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.amount]
    strb    r0, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    
    strb    r4, [r1, #HandlerParam_ChangeStatStage.pokeId]
    
    mov     r0, r5
    bl      Battle::Handler_PopWork

Return:
    pop     {r4-r5, pc}
    