    push    {r4-r6, lr}
    mov     r4, r2
    mov     r6, r0
    mov     r0, r4
    mov     r5, r1
    bl      Battle::HandlerCommon_CheckTargetMonID
    cmp     r0, #FALSE
    beq     Return

    mov     r0, #VAR_DelayAttackFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    bne     Return

    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_IsMonInSkyDrop ; TODO - ensure this function at 021ABEF4 is named properly
    cmp     r0, #FALSE
    bne     Return

    mov     r0, r5
    bl      Battle::Handler_CheckMatchup ; TODO - ensure this function at 021ABF54 is named properly
    cmp     r0, #FALSE
    bne     Return

    mov     r0, r5
    bl      Battle::IsPokeSwitchingOut
    cmp     r0, #FALSE
    bne     Return

    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_GetFightEnableBenchPokeNum  ; TODO - ensure this function at 021ABAA4 is named properly
    cmp     r0, #FALSE
    beq     Return

    mov     r0, r5
    bl      Battle::Handler_CheckReservedMemberChangeAction ; TODO - ensure this function at 021ABACC is named properly
    cmp     r0, #FALSE
    beq     Return

    mov     r0, r6
    mov     r1, r5
    mov     r2, r4
    bl      Battle::ItemEvent_PushRun

Return:
    pop     {r4-r6, pc}