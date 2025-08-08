    push    {r3-r7, lr}
    mov     r5, r1
    mov     r6, r2 ; r6 := pokeID
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::BattleEventVar_GetValue
    cmp     r6, r0
    bne     Return

    mov     r0, #VAR_TargetPokeId_0
    bl      Battle::BattleEventVar_GetValue
    lsl     r0, r0, #0x18
    lsr     r4, r0, #0x18
    cmp     r4, #0x1F //VAR_MoveEffect?
    beq     Return

    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r1, #1
    mov     r7, #MC_Burn
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     Return

    mov     r0, r5 ; r0 := server flow
    mov     r1, #HE_CureStatus
    mov     r2, r6 ; r2 := pokeID
    bl      Battle::Handler_PushWork
    mov     r1, r0 r1 := handler param
    strb    r4, [r1, #8]
    strb    r7, [r1, #0x14]
    mov     r0, r5
    str     r7, [r1, #4]
    bl      Battle::Handler_PopWork

Return:
    pop     {r3-r7, pc}