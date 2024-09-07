#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_MAX_HIT_COUNT 0x29
#DEFINE VAR_HIT_COUNT 0x2A
#DEFINE VAR_AVOID_FLAG 0x42
#DEFINE VAR_GENERAL_USE_FLAG 0x51

#DEFINE EVENT_MOVE_HIT_COUNT 0x35

#DEFINE MVDATA_HIT_MAX 0x08

; r0: *serverFlow
; r1: *attackingMonParam
; r2: moveId

    push    {r3-r7, lr}    
    mov     r6, r0
    mov     r7, r1
    
    mov     r0, r2
    mov     r1, #MVDATA_HIT_MAX
    bl      ARM9::GetMoveMetadata
    mov     r4, r0
    cmp     r4, #1
    bhi     IsMultiStrike
    
    mov     r0, #4
    lsl     r0, #10 ; 4096 (1.0x)
    pop     {r3-r7, pc}
    
    
IsMultiStrike:
    bl      Battle::EventVar_Push
    
    mov     r0, r7
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_MAX_HIT_COUNT
    mov     r1, r4
    bl      Battle::EventVar_SetConstValue
    
    mov     r0, #VAR_HIT_COUNT
    mov     r1, r4
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
    
    lsl     r5, r4, #12 ; r5 := maxCount * 4096
    cmp     r4, #5
    bne     PopAndReturn ; if not five-strike
    
    mov     r0, #VAR_GENERAL_USE_FLAG ; 1=Skill Link, 2=Loaded Dice
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    
CheckSkillLink:
    lsl     r5, r4, #12 ; 0x5000 (5.0x)
    cmp     r1, #1
    beq     PopAndReturn
    
CheckLoadedDice:
    mov     r5, #9
    lsl     r5, #11 ; 0x4800 (4.5x)
    cmp     r1, #2
    beq     PopAndReturn
    
    ; otherwise, use this default
    ldr     r5, =0x319A ; 12698 (3.1x)

PopAndReturn:
    bl      Battle::EventVar_Pop
    
Return:
    mov     r0, r5    
    pop     {r3-r7, pc}
    