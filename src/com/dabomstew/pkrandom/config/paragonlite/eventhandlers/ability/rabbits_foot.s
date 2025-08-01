    push    {r4-r5, lr}
    mov     r5, r1
    mov     r4, r2

    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return

    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r1, #MF_Kick
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return

    mov     r0, #VAR_GeneralUseFlag
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue

Return:
    pop     {r4-r5, pc}