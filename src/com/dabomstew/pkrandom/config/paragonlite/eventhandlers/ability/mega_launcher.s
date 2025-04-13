    push    {r4-r5, lr}
    mov     r4, r2
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x12 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    mov     r5, r0
    
#if ABILITY_MEGA_LAUNCHER_INCLUDES_BALL_BOMB_MOVES
    mov     r1, #20 ; ball/bomb move
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    bne     ApplyPowerModifier
#endif
    
    mov     r0, r5
    mov     r1, #21 ; pulse move
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     Return
    
ApplyPowerModifier:
    mov     r0, #0x31 ; move power
    ldr     r1, =5325 ; 1.3x
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r5, pc}