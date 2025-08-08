    push    {r3-r5, lr}
    mov     r4, r1 ; r4 := server flow
    mov     r5, r2 ; r5 := poke

    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return

    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    lsl     r1, r1, #0x18
    mov     r0, r4
    lsr     r1, r1, #0x18
    bl      Battle::GetPoke
    mov     r4, r0
    bl      Battle::IsSubstituteActive
    cmp     r0, #0
    bne     Return

    ; check if move failed/conditions
    mov     r0, #VAR_FailCause
    bl      Battle::EventVar_GetValue
    cmp     r0, #MFC_Sleep
    beq     Boost
    cmp     r0, #MFC_Paralysis
    beq     Boost
    cmp     r0, #MFC_Freeze
    beq     Boost
    cmp     r0, #MFC_Confusion
    beq     Boost
    cmp     r0, #MFC_Flinch
    beq     Boost
    cmp     r0, #MFC_FocusInterrupted
    beq     Boost
    cmp     r0, #MFC_Infatuation
    beq     Boost
    cmp     r0, #MFC_Taunt
    beq     Boost
    cmp     r0, #MFC_Torment
    beq     Boost
    cmp     r0, #MFC_Imprison
    beq     Boost
    cmp     r0, #MFC_HealBlock
    beq     Boost
    cmp     r0, #MFC_Ability
    beq     Boost
    cmp     r0, #MFC_Other // ?
    bne     Return

Boost:
    ; double base power
    mov     r0, #2
    bl      Battle::CommonMultiplyBasePower

Return:
    pop     {r3-r5, pc}