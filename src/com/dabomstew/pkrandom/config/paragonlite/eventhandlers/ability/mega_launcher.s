    push    {r4-r5, lr}
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r5, r0
    
#if ABILITY_MEGA_LAUNCHER_INCLUDES_BALL_BOMB_MOVES
    mov     r1, #MF_BallBomb
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    bne     ApplyPowerModifier
#endif
    
    mov     r0, r5
    mov     r1, #MF_Pulse
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return
    
ApplyPowerModifier:
    mov     r0, #VAR_MovePower
    ldr     r1, =(0x1000 * ABILITY_MEGA_LAUNCHER_MULTIPLIER)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r5, pc}