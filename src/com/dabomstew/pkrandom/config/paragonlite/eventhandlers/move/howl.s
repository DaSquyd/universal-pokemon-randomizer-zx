    push    {r3-r4, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, r5
    bl      Battle::Handler_GetTempWork
    mov     r6, r0
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_PokeIDToPokePos
    mov     r1, #EXND_TargetAndAllies
    lsl     r1, #8
    orr     r1, r0
    mov     r0, r5
    mov     r2, r6
    bl      Battle::Handler_ExpandPokeID
    mov     r7, r0
    beq     Return
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    mov     r2, #STSG_Attack ; 1
    str     r2, [r1, #HandlerParam_ChangeStatStage.stat]
    strb    r2, [r1, #HandlerParam_ChangeStatStage.amount]
    strb    r7, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    strb    r2, [r1, #HandlerParam_ChangeStatStage.fMoveAnimation]
    mov     r0, #0
    strb    r0, [r1, #HandlerParam_ChangeStatStage.exStr]
    
    mov     r3, #0
    cmp     r7, #0
    bls     PopWork
    
LoopStart:
    ldrb    r2, [r6, r3]
    add     r0, r1, r3
    add     r3, #1
    strb    r2, [r0, #HandlerParam_ChangeStatStage.pokeIds[0]]
    cmp     r3, r7
    bcc     LoopStart
    
PopWork:
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r4, pc}