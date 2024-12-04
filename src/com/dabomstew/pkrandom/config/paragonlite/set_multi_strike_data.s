#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_MAX_HIT_COUNT 0x29
#DEFINE VAR_HIT_COUNT 0x2A
#DEFINE VAR_AVOID_FLAG 0x42
#DEFINE VAR_GENERAL_USE_FLAG 0x51

#DEFINE EVENT_MOVE_HIT_COUNT 0x35

    push    {r3-r7, lr}
    mov     r5, r3
    mov     r6, r0
    mov     r7, r1
    
    mov     r0, r2
    mov     r1, #8 ; max hits
    bl      ARM9::GetMoveData
    lsl     r0, #24
    lsr     r4, r0, #24
    
    mov     r0, #0
    strb    r0, [r5, #1]
    mov     r0, #3
    strb    r0, [r5, #5]
    
    cmp     r4, #1 ; single-strike
    bhi     IsMultiStrike
    
IsSingleStrike:
    mov     r0, #1
    strb    r0, [r5, #0]
    
    mov     r0, #0
    strb    r0, [r5, #2]
    strb    r0, [r5, #3]
    
    pop     {r3-r7, pc}
    
    
IsMultiStrike:
    mov     r0, r4
    bl      Battle::RandMultiStrikeHits
    str     r0, [sp, #0] ; hitCheckParam->countMax := r0
    
    bl      Battle::EventVar_Push
    
    mov     r0, r7
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_MAX_HIT_COUNT
    mov     r1, r4
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #0] ; r1 := hitCheckParam->hitCount
    mov     r0, #VAR_HIT_COUNT
    bl      Battle::EventVar_SetValue
    
    mov     r0, #VAR_GENERAL_USE_FLAG ; 1=Skill Link, 2=Loaded Dice
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, #VAR_AVOID_FLAG ; check acc per hit
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, r6
    mov     r1, #EVENT_MOVE_HIT_COUNT
    bl      Battle::Event_CallHandlers
    
    cmp     r4, #5
    bhi     MultiStrikeLogic ; as far as I'm aware, this only constitutes Beat Up
    
    mov     r0, #VAR_GENERAL_USE_FLAG ; 1=Skill Link, 2=Loaded Dice
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    beq     MultiStrikeLogic
    cmp     r0, #1
    beq     SkillLink
    cmp     r4, #5 ; double- and triple-strike moves should simply do their max like Skill Link
    bcc     SkillLink
    
LoadedDice:
; only applies to moves with a max hit count of 5
    mov     r0, #2
    bl      Battle::Random ; 50%/50% for 4 vs 5 hits
    cmp     r0, #0
    beq     SkillLink
    mov     r4, #4

SkillLink:
    strb    r4, [r5, #0] ; hits := max hits (r4)
    mov     r0, #0
    b       MultiStrikeEnd

MultiStrikeLogic:
    mov     r0, #VAR_HIT_COUNT
    bl      Battle::EventVar_GetValue
    strb    r0, [r5, #0] ; hitCheckParam->countMax := r0
    
    mov     r0, #VAR_AVOID_FLAG ; check acc per hit
    bl      Battle::EventVar_GetValue
    
MultiStrikeEnd:
    strb    r0, [r5, #2] ; hitCheckParam->fCheckEveryTime := r0
    
    mov     r0, #1
    strb    r0, [r5, #3] ; hitCheckParam->fMultiHitMove := true
    
    ldr     r0, =0x30C6
    bl      Battle::EventVar_Pop
    
    mov     r0, #1
    pop     {r3-r7, pc}
    