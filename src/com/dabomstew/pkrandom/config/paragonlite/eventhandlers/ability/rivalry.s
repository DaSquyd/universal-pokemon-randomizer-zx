    push    {r4-r6, lr}
    mov     r0, #VAR_AttackingPoke
    mov     r5, r1
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r4, r0
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    mov     r5, r0
    
; Get Gender of Pokémon 1
    mov     r0, r4
    mov     r1, #BPV_Gender
    bl      Battle::GetPokeStat
    
; Get Gender of Pokémon 2
    mov     r0, r5
    mov     r1, #BPV_Gender
    bl      Battle::GetPokeStat
    
; Check if either is gender unknown (value of 2)
    cmp     r4, #2
    beq     Return
    cmp     r0, #2
    beq     Return
    
    cmp     r4, r0
    
#if ABILITY_RIVALRY_SAME_GENDER_MULTIPLIER != 1 && ABILITY_RIVALRY_OPPOSITE_GENDER_MULTIPLIER != 1
    bne     Opposite
    
    mov     r1, =(0x1000 * ABILITY_RIVALRY_SAME_GENDER_MULTIPLIER)
    b       MulValue
    
Opposite:
    mov     r1, =(0x1000 * ABILITY_RIVALRY_OPPOSITE_GENDER_MULTIPLIER)
    

#elif ABILITY_RIVALRY_SAME_GENDER_MULTIPLIER != 1
    bne     Return
    
    mov     r1, =(0x1000 * ABILITY_RIVALRY_SAME_GENDER_MULTIPLIER)
    
    
#else 
    beq     Return
    
    mov     r1, =(0x1000 * ABILITY_RIVALRY_OPPOSITE_GENDER_MULTIPLIER)
#endif
    
    
MulValue:
    mov     r0, #VAR_MovePower
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r6, pc}
