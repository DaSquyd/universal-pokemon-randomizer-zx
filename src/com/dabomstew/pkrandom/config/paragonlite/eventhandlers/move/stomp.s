    push    {r3-r5, lr}
    mov     r4, r1 ; r4 := server flow
    mov     r5, r2 ; r5 := poke

    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return

    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    mov     r1, r0 ; r1 := defender poke

    ; check if target has the minimised flag
    mov     r1, #CF_Minimize
    bl      Battle::Poke_GetConditionFlag ; defender poke result still in r0
    cmp     r0, #0
    bl      Return

    ; double damage if target minimised
    mov     r0, #EVENT_OnMoveHitCount
    mov     r3, #8
    lsl     r1, r3, #0xA ; r1 := r7 << 0xA = 8 * 1024 = 8192 = 0x2000 = x2
    bl      Battle::EventVar_MulValue

    ; bypass accuracy checks if target minimised
    mov     r0, #VAR_GeneralUseFlag
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue

Return:
    pop     {r3-r5, pc}