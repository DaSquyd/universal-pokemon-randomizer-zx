#define S_AllyCount 0x00
#define S_PokeCount 0x04

    push    {r3-r4, lr}
    sub     sp, #0x08
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
    str     r0, [sp, #S_AllyCount]
    cmp     r0, #0
    beq     Return
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    mov     r0, #STSG_Attack ; 1
    str     r0, [r7, #HandlerParam_ChangeStatStage.stat]
    strb    r0, [r7, #HandlerParam_ChangeStatStage.amount]
    strb    r0, [r7, #HandlerParam_ChangeStatStage.fMoveAnimation]
    
    mov     r3, #0
    str     r3, [sp, #S_PokeCount]
    cmp     r0, #0
    bls     PopWork
    
LoopStart:
    mov     r0, r5
    ldrb    r1, [r6, r3]
    bl      Battle::GetPoke
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    cmp     r0, #283 ; Good as Gold
    beq     LoopEnd
    
    ldr     r0, [sp, #S_PokeCount]
    add     r1, r7, r0
    
    ldrb    r2, [r6, r3]
    strb    r2, [r1, #HandlerParam_ChangeStatStage.pokeIds[0]]
    
    add     r0, #1
    str     r0, [sp, #S_PokeCount]
    
LoopEnd:
    add     r3, #1
    ldr     r0, [sp, #S_AllyCount]
    cmp     r3, r0
    bcc     LoopStart
    
    ldr     r0, [sp, #S_PokeCount]
    strb    r0, [r7, #HandlerParam_ChangeStatStage.pokeCount]
    
PopWork:
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
Return:
    add     sp, #0x08
    pop     {r3-r4, pc}