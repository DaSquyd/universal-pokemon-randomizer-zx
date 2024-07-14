    push    {r3-r7, lr}
    mov     r5, r3
    mov     r6, r0
    mov     r7, r1
    
    mov     r0, r2
    mov     r1, #8 ; max hits
    bl      ARM9::GetMoveMetadata
    lsl     r0, #24
    lsr     r4, r0, #24
    
    mov     r0, #0
    strb    r0, [r5, #1]
    mov     r0, #3
    strb    r0, [r5, #5]
    
    cmp     r4, #1 ; single-strike
    bhi     IsMultiStrike
    
; Single Strike
    mov     r0, #1
    strb    r0, [r5, #0]
    
    mov     r0, #0
    strb    r0, [r5, #2]
    strb    r0, [r5, #3]
    
    pop     {r3-r7, pc}
    
IsMultiStrike:
    mov     r0, r4
    bl      Battle::RandMultiStrikeHits
    str     r0, [sp, #0] ; hits
    
    ldr     r0, =0x30AF
    bl      Battle::EventVar_Push
    
    mov     r0, r7
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #3
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #0x29
    mov     r1, r4
    bl      Battle::EventVar_SetConstValue
    
    ldr     r1, [sp, #0] ; hits
    mov     r0, #0x2A
    bl      Battle::EventVar_SetValue
    
    mov     r0, #0x51 ; 1=Skill Link, 2=Loaded Dice
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, #0x42 ; hits
    mov     r1, #0
    bl      Battle::EventVar_SetRewriteOnceValue
    
    mov     r0, r6
    mov     r1, #0x35 ; onGetHitCount
    bl      Battle::Event_CallHandlers
    
    cmp     r4, #5
    bhi     MultiStrikeLogic ; as far as I'm aware, this only constitutes Beat Up
    
    mov     r0, #0x51 ; 1=Skill Link, 2=Loaded Dice
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
    bl      Battle::Rand ; 50%/50% for 4 vs 5 hits
    cmp     r0, #0
    beq     SkillLink
    mov     r4, #4

SkillLink:
    strb    r4, [r5, #0] ; hits := max hits (r4)
    mov     r0, #0
    b       MultiStrikeEnd

MultiStrikeLogic:
    mov     r0, #0x2A
    bl      Battle::EventVar_GetValue
    strb    r0, [r5, #0]
    
    mov     r0, #0x42
    bl      Battle::EventVar_GetValue
    
MultiStrikeEnd:  
    strb    r0, [r5, #2]
    
    mov     r0, #1
    strb    r0, [r5, #3]
    
    ldr     r0, =0x30C6
    bl      Battle::EventVar_Pop
    
    mov     r0, #1
    pop     {r3-r7, pc}
    