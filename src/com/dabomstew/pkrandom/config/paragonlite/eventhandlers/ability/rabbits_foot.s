    push    {r4, lr}
    mov     r0, #VAR_AttackingPoke
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return

    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue

    mov     r1, #MF_Kick
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return

    mov     r0, #VAR_Accuracy
    mov     r1, #100
    bl      Battle::EventVar_RewriteValue

Return:
    pop     {r4, pc}