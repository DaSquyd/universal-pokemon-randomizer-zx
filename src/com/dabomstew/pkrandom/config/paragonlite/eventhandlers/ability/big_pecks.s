    push    {r3-r5,lr}
    mov     r5, r1
    mov     r4, r2

    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return

    mov     r0, #3
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r4
    bl      Battle::MainModule_IsAllyMonID
    cmp     r0, #0
    bne     Return

    mov     r0, #VAR_Volume
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bge     Return

    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    ldr     r2, [r1, #HandlerParam_ChangeStatStage.header]
    mov     r0, #BHP_AbilityPopup
    orr     r0, r2
    str     r0, [r1]
    mov     r0, #STAT_Defense ; 2
    str     r0, [r1,#HandlerParam_ChangeStatStage.stat] ; defense
    strb    r0, [r1,#HandlerParam_ChangeStatStage.amount] ; 2
    mov     r0, #1 ; true
    strb    r0, [r1,#HandlerParam_ChangeStatStage.fAlways] ; true
    strb    r0, [r1,#HandlerParam_ChangeStatStage.pokeCount] ; 1
    mov     r0, r5
    strb    r4, [r1,#HandlerParam_ChangeStatStage.pokeId]
    bl      Battle::Handler_PopWork

Return:
    pop     {r3-r5,pc}