    push    {r4-r5, lr} ; store r4 and the linking register onto the stack
    mov     r4, r2 ; check if the caller is the attacker
    
    mov     r0, #VAR_AttackingPoke ; store the attacker into r0
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return

    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r5, r0
    mov     r1, #MF_Light ; store if the chosen move is a light move in r1
    bl      ARM9::MoveHasFlag ; check if light move
    cmp     r0, #FALSE ; compare these values, store result in the Zero condition flag
    bne     ApplyMultiplier
    
    mov     r0, r5
    mov     r1, #MF_Beam
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return
        
ApplyMultiplier:
    mov     r0, #VAR_Ratio ; ratio is used for *most* multipliers
    ldr     r1, =(0x1000 * ABILITY_FOCUSING_LENS_MULTIPLIER) ; load value of power multiplier into r1
    bl      Battle::EventVar_MulValue ; apply the multiplier in r1 to r0 then Return

Return:
    pop     {r4-r5, pc} ; take off top two values off stack and store in r4 and pc. here that is r4-r4, lr-pc