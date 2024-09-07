#DEFINE TRAINER_AI_ENV_CONSIDERED_MOVE_IDX 0x01
#DEFINE TRAINER_AI_ENV_SCORES_ARR 0x04
#DEFINE TRAINER_AI_ENV_RESULT 0xC4

; r0: *scriptVM
; r1: *trainerAIEnv

    push    {r4-r5, lr}
    mov     r4, r1 ; r4 := *trainerAIEnv
    
    bl      ARM9::Script_ReadArg
    ldrb    r1, [r4, #TRAINER_AI_ENV_CONSIDERED_MOVE_IDX]
    add     r5, r4, #TRAINER_AI_ENV_SCORES_ARR ; r5 := trainerAIEnv->scoresArr
    lsl     r3, r1, #2
    ldr     r1, [r5, r3] ; r1 := trainerAIEnv->scoresArr[trainerAIEnv->consideredMoveIdx]
    bl      Battle::FixedRound
    str     r0, [r5, r3]
    
    ldrb    r0, [r4, #1]
    lsl     r1, r0, #2
    ldr     r0, [r5, r1]
    cmp     r0, #0
    bge     Return
    
    mov     r0, #0
    str     r0, [r5, r1]
    
Return:
    add     r4, #TRAINER_AI_ENV_RESULT
    ldr     r0, [r4]
    pop     {r4-r5, pc}