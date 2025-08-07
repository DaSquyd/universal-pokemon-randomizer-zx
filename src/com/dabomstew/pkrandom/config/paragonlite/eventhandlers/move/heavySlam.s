    push    {r4-r7, lr}
    mov     r4, r1 ; r4 := server flow
    mov     r5, r2 ; r5 := poke

    ; get poke that is move user
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return

    mov     r0, r4 ; r0 := server flow
    mov     r1, r5 ; r1 := poke
    bl      Battle::GetPoke
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    mov     r1, r0 ; r1 := defender?
    lsl     r1, r1, #0x18
    lsr     r1, r1, #0x18
    mov     r0, r4 ; r0 := server flow
    bl      Battle::GetPoke
    mov     r6, r0 ; r6 := target?
    mov     r0, r4 ; r0 := server flow
    mov     r1, r5 ; r1 := poke
    bl      Battle::GetPokeWeight
    mov     r5, r0 ; r5 := attackers? weight
    mov     r0, r6 ; r0 := target?
    bl      Battle::GetPokeId
    mov     r1, r0 ; r1 := pokeID
    mov     r0, r4 ; r0 := server flow
    bl      Battle::GetPokeWeight
    mov     r1, r0 ; r1 := defenders? weight (denominator)
    mov     r0, r5 ; r0 := attackers? weight (numerator)
    blx     ARM9::DivideModSigned
    cmp     r0, #5 ; check if division < 5
    blt     CaseFour
    mov     r1, #0x78 ; 120 BP
    b       RewriteBPVal

CaseFour:
    cmp     r0, #4
    bne     CaseThree
    mov     r1, #0x64 ; 100 BP
    b       RewriteBPVal

CaseThree:
    cmp     r0, #3
    bne     CaseTwo
    mov     r1, #0x50 ; 80 BP
    b       RewriteBPVal

CaseTwo:
    mov     r1, #0x3C ; 60 BP
    cmp     r0, #2
    beq     RewriteBPVal
    mov     r1, #0x28 ; 40 BP

RewriteBPVal:
    mov     r0, #VAR_MoveBasePower
    bl      Battle::EventVar_RewriteValue

MinimizedLogic:
    ; check if target has the minimised flag
    mov     r0, r6 ; r0 := target?
    mov     r1, #CF_Minimize
    bl      Battle::Poke_GetConditionFlag
    cmp     r0, #0
    bl      Return

    ; double damage if target minimised
    mov     r0, #EVENT_OnMoveHitCount
    mov     r7, #8
    lsl     r1, r7, #0xA ; r1 := r7 << 0xA = 8 * 1024 = 8192 = 0x2000 = x2
    bl      Battle::EventVar_MulValue

    ; bypass accuracy checks if target minimised
    mov     r0, #VAR_GeneralUseFlag
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue

Return:
    pop     {r4-r7, sp}