    push    {r4, lr} ; store r4 and the linking register onto the stack
    mov     r0, #VAR_AttackingPoke ; store the attacker into r0
    mov     r4, r2 ; check if the caller is the attacker
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return ; if not, end

    mov     r0, #VAR_MoveID ; store moveID in r0
    bl      Battle::EventVar_GetValue
    mov     r1, #MF_Light ; store if the chosen move is a light move in r1
    bl      ARM9::MoveHasFlag ; check if light move
    cmp     r0, #FALSE ; compare these values, store result in the Zero condition flag
    beq     Return ; if not, end

    ldr     r1, =(0x1000 * FOCUSING_LENS_MULTIPLIER) ; load value of power multiplier into r1
    mov     r0, #VAR_MovePower ; move BP into r0
    bl      Battle::EventVar_MulValue ; apply the multiplier in r1 to r0 then Return

Return:
    pop     {r4, pc} ; take off top two values off stack and store in r4 and pc. here that is r4-r4, lr-pc