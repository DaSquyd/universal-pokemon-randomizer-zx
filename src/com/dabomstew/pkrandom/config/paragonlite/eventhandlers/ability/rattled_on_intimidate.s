    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_IntimidateFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    beq     Return
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    mov     r0, #1
    lsl     r0, #23
    ldr     r2, [r1, #HandlerParam_ChangeStatStage.header]
    orr     r0, r2
    str     r0, [r1, #HandlerParam_ChangeStatStage.header]
    mov     r0, #STSG_Speed
    str     r0, [r1, #HandlerParam_ChangeStatStage.stat]
    mov     r2, #1
    strb    r2, [r1, #HandlerParam_ChangeStatStage.amount]
    strb    r2, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    strb    r4, [r1, #HandlerParam_ChangeStatStage.pokeId]
    
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r5, pc}