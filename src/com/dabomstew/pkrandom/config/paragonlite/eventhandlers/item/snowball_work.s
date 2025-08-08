    push    {r3-r5, lr}
    mov     r5, r1
    mov     r4, r2

    mov     r0, #VAR_PokeId
    bl      Battle::BattleEventVar_GetValue
    cmp     r4, r0
    bne     Return

    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0
    mov     r0, #1
    strb    r0, [r1, #0x0F]
    strb    r4, [r1, #0x10]
    str     r0, [r1, #4]
    strb    r0, [r1, #0x0C]
    mov     r0, r5
    bl      Handler_PopWork

Return:
    pop     {r3-r5, pc}