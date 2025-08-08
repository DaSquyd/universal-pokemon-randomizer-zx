    push    {r4-r6, lr}
    mov     r6, r0 ; r6 := battleEventItem
    mov     r5, r1 ; r5 := server flow
    mov     r4, r2 ; r4 := poke

    mov     r0, #VAR_DefendingPoke
    bl      Battle::BattleEventVar_GetValue
    mov     r4, r0
    bne     Return

    mov     r0, #VAR_SubstituteFlag
    bl      Battle::BattleEventVar_GetValue
    cmp     r0, #FALSE
    bne     Return

    mov     r0, #VAR_MoveType
    bl      Battle::BattleEventVar_GetValue
    cmp     r0, #TYPE_Ice
    bne     Return

    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r1, #1
    mov     r2, #1
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    beq     Return

    mov     r0, r6
    mov     r1, r5
    mov     r2, r4
    bl      Battle::ItemEvent_PushRun

Return:
    pop     {r4-r6, pc}