#DEFINE FRONT_POKE_POS 0x00

    push    {r3-r7, lr}
    mov     r6, r1
    mov     r7, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r7, r0
    bne     Return
    
    mov     r0, r6
    mov     r1, r7
    bl      Battle::Handler_GetExistFrontPokePos
    str     r0, [sp, #FRONT_POKE_POS]
    
    mov     r0, r6 ; r0 := serverFlow
    bl      Battle::Handler_GetTempWork
    mov     r4, r0
    
    ldr     r1, [sp, #FRONT_POKE_POS]
    mov     r0, #1
    lsl     r0, #8 ; 0x0100
    orr     r1, r0
    mov     r0, r6
    mov     r2, r4
    bl      Battle::Handler_ExpandPokeID
    mov     r5, r0
    beq     Return
    
    mov     r0, r6
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r7
    bl      Battle::Handler_PushRun
    
    mov     r0, r6 ; r0 := serverFlow
    mov     r1, #HE_ChangeStatStage
    mov     r2, r7
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    ; NEW - Intimidate flag
    ldr     r0, [r1, #HandlerParam_ChangeStatStage.header]
    mov     r2, #(BHP_Intimidate >> 23)
    lsl     r2, #23
    orr     r0, r2
    str     r0, [r1, #HandlerParam_ChangeStatStage.header]
    
    mov     r2, #STSG_Attack
    str     r2, [r1, #HandlerParam_ChangeStatStage.stat]
    sub     r0, r2, #2 ; -1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.amount]
    strb    r2, [r1, #HandlerParam_ChangeStatStage.fAlways]
    strb    r5, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    
    mov     r3, #0
    cmp     r5, #0
    bls     LoopEnd
    
LoopStart:
    ldrb    r2, [r4, r3]
    add     r0, r1, r3
    add     r3, #1
    strb    [r0, #HandlerParam_ChangeStatStage.pokeIds[0]]
    cmp     r3, r5
    bcc     LoopStart
    
LoopEnd:    
    mov     r0, r6
    bl      Battle::Handler_PopWork
    mov     r0, r6
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r7
    bl      Battle::Handler_PushRun
    
Return:
    pop     {r3-r7, pc}