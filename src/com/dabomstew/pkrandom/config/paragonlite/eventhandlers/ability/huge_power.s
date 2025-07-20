    push    {r4, lr} ; push will take the provided registers and push them onto the stack
    mov     r4, r2 ; r2 in this context is going to be the pokeId of the mon with this ability
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue ; this function takes in r0 as an input, and it's going to be the VAR type
    cmp     r4, r0 ; we compare the pokeId of the mon with this ability to the return value from the above function
    bne     Return ; if it's not a match, we can just return; we only want to continue here if the attacker has this ability
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue ; we'll get the move id and store it in r0
    bl      ARM9::GetMoveCategory ; this function takes in r0 as a move id and returns its category (read from moves narc)
    cmp     r0, #CAT_Physical
    bne     Return ; return if the category of the move isn't physical
    
    mov     r0, #VAR_Ratio
    ldr     r1, =(0x1000 * ABILITY_HUGE_POWER_MULTIPLIER) ; 0x1000 is 1.0x, 0x2000 is 2x, 0x1800 is 1.5x, 0x14CD is 1.3x, 0x800 is 0.5x, and so on...
    bl      Battle::EventVar_MulValue ; takes two arguments... r0 is the var type (ratio here), and r1 is the multiplier
    
Return:
    pop     {r4, pc} ; pop should match push; the registers will be repopulated in the same order, but we place what we stored from LR into PC so we can return properly