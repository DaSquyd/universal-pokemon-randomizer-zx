    push    {r4-r7, lr}
    mov     r6, r0
    mov     r5, r1
    mov     r4, r2

    ; get defender
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return

    ; check if using a powder move
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    mov     r1, #MF_Powder
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     Return

    ; get Pokemon
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r7, r0
    
PushRun:
    mov     r0, r6
    mov     r1, r5
    mov     r2, r4
    bl      Battle::ItemEvent_PushRun
    
Return:
    pop     {r4-r7, pc}