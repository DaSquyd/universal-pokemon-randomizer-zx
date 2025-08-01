#define S_ReadCount 0x00
#define S_ReadPoke 0x04
#define S_WritePoke 0x08
#define StackSize 0x0C

    push    {r4-r7, lr}
    sub     sp, #StackSize
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
    str     r0, [sp, #S_ReadCount]
    cmp     r0, #0
    bne     PushWork
    
; likely singles? this particular expansion always fails in singles/rotation so we need to hard set it to the caller
    mov     r0, #1
    str     r0, [sp, #S_ReadCount]
    strb    r4, [r6]
    
PushWork:    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    mov     r0, #STSG_Attack ; 1
    str     r0, [r7, #HandlerParam_ChangeStatStage.stat]
    strb    r0, [r7, #HandlerParam_ChangeStatStage.amount]
    strb    r0, [r7, #HandlerParam_ChangeStatStage.fAlways]
    
    mov     r0, #0
    str     r0, [sp, #S_ReadPoke]
    str     r0, [sp, #S_WritePoke]
    
LoopStart:    
    mov     r0, r5
    ldr     r1, [sp, #S_ReadPoke]
    ldrb    r1, [r6, r1]
    bl      Battle::GetPoke
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    ; TODO: make this not work as dumb as this
    mov     r1, #43 ; soundproof
    cmp     r0, r1
    beq     LoopEnd
    add     r1, #(283 - 43) ; Good as Gold
    cmp     r0, r1
    beq     LoopEnd
    add     r1, #(507 - 283) ; Cacophony
    cmp     r0, r1
    beq     LoopEnd
    
    ldr     r0, [sp, #S_WritePoke]
    add     r1, r7, r0
    
    ldr     r2, [sp, #S_ReadPoke]
    ldrb    r2, [r6, r2]
    strb    r2, [r1, #HandlerParam_ChangeStatStage.pokeIds[0]]
    
    add     r0, #1
    str     r0, [sp, #S_WritePoke]
    
LoopEnd:
    ldr     r0, [sp, #S_ReadPoke]
    add     r0, #1
    str     r0, [sp, #S_ReadPoke]
    ldr     r1, [sp, #S_ReadCount]
    cmp     r0, r1
    bcc     LoopStart
    
    ldr     r0, [sp, #S_WritePoke]
    strb    r0, [r7, #HandlerParam_ChangeStatStage.pokeCount]
    
PopWork:
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
Return:
    add     sp, #StackSize
    pop     {r4-r7, pc}