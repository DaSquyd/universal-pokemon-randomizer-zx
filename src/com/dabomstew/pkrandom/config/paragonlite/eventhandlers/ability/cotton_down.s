#define frontPokePos 0x00

    push    {r3-r7, lr}
    mov     r6, r1 ; r6 := server flow
    mov     r7, r2 ; r7 := pokeID

    ; check poke is ability owner
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r7, r0
    bne     Return

    ; get position of front poke
    mov     r0, r6 ; r0 := server flow
    mov     r1, r7 ; r1 := pokeID
    bl      Battle::Handler_GetExistFrontPokePos
    str     r0, [sp, #frontPokePos]
    mov     r0, r6 ; r0 := serverFlow
    bl      Battle::Handler_GetTempWork
    mov     r4, r0 ; r4 := tempWork
    ldr     r1, [sp, #frontPokePos]
    mov     r0, #1
    lsl     r0, #8 ; r0 := 0x0100
    orr     r1, r0 ; r1 := frontPokePos
    mov     r0, r6 ; r0 := server flow
    mov     r2, r4 ; r2 := tempWork
    bl      Battle::Handler_ExpandPokeID
    mov     r5, r0 ; hander
    beq     Return
    
    mov     r0, r6 ; r0 := server flow
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r7 ; r2 := pokeID
    bl      Battle::Handler_PushRun
    
    mov     r0, r6 ; r0 := serverFlow
    mov     r1, #HE_ChangeStatStage
    mov     r2, r7 ; r2 := pokeID
    bl      Battle::Handler_PushWork
    mov     r1, r0 ; r1 := work

    mov     r2, #STSG_Speed
    str     r2, [r1, #HandlerParam_ChangeStatStage.stat]
    sub     r0, r2, #2 ; -1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.amount]
    strb    r2, [r1, #HandlerParam_ChangeStatStage.fAlways]
    strb    r5, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    
    mov     r3, #0
    cmp     r5, #0
    bls     LoopEnd
    
LoopStart:
    ldrb    r2, [r4, r3] ; load tempWork idx into r2
    add     r0, r1, r3 ; add work and tempWork into r0
    add     r3, #1 ; increment idx
    strb    [r0, #HandlerParam_ChangeStatStage.pokeIds[0]]
    cmp     r3, r5 ; check if idx == loop length
    bcc     LoopStart
    
LoopEnd:    
    mov     r0, r6 ; r0 := serverFlow
    bl      Battle::Handler_PopWork
    mov     r0, r6 ; r0 := serverFlow
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r7 ; r2 := pokeID
    bl      Battle::Handler_PushRun

Return:
    pop     {r3-r7, pc}