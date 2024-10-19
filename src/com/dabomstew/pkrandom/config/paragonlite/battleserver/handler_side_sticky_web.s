    push    {r4-r7, lr}
    mov     r5, r1
    mov     r6, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    bl      Battle::GetTeamIdFromPokePos
    cmp     r6, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_CheckFloating
    cmp     r0, #0
    bne     Return
    
    
    mov     r0, r5
    mov     r1, #HE_Message
    mov     r2, #0x1F
    bl      Battle::Handler_PushWork
    mov     r4, r0
    
    add     r0, r4, #HandlerParam_Message.exStr
    mov     r1, #2
    ldr     r2, =BTLTXT_StickyWeb_Laid
    bl      Battle::Handler_StrSetup
    
    add     r0, r4, #HandlerParam_Message.exStr
    mov     r1, r7
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_PopWork
    
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, #0x1F
    bl      Battle::Handler_PushWork
    mov     r4, r0
    
    mov     r0, #STSG_Speed
    str     r0, [r4, #HandlerParam_ChangeStatStage.stat]
    
    sub     r0, #(STSG_Speed + 1) ; -1
    strh    r0, [r4, #HandlerParam_ChangeStatStage.amount]
    
    mov     r0, #1
    strb    r0, [r4, #HandlerParam_ChangeStatStage.pokeCount]
    
    strb    r7, [r4, #HandlerParam_ChangeStatStage.pokeId]
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r7, pc}