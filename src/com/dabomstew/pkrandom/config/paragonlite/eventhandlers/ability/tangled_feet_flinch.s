    push    {r4-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_FailCause
    bl      Battle::EventVar_GetValue
    cmp     r0, #MFC_Flinch
    bne     End
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, r5
    mov     r1, #14
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    ldr     r2, [r1, #HandlerParam_ChangeStateStage.header]
    mov     r0, #(BHP_AbilityPopup >> 23)
    lsl     r0, #23
    orr     r0, r2
    str     r0, [r1, #HandlerParam_ChangeStateStage.header]
    mov     r0, #STSG_Speed
    str     r0, [r1, #HandlerParam_ChangeStateStage.stat]
    mov     r2, #2 ; stat boost amount
    strb    r2, [r1, #HandlerParam_ChangeStateStage.amount]
    mov     r0, #0
    strb    r0, [r1, #HandlerParam_ChangeStateStage.fMoveAnimation]
    strb    r2, [r1, #HandlerParam_ChangeStateStage.pokeCount]
    mov     r0, r5
    strb    r4, [r1, #HandlerParam_ChangeStateStage.pokeId]
    bl      Battle::Handler_PopWork
    
End:
    pop     {r4-r5, pc}
    