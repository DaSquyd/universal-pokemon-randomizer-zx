    push    {r4, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r1, #MF_Punch
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_MovePower
    ldr     r1, =(0x1000 * ABILITY_IRON_FIST_MULTIPLIER)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4, pc}