#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE VAR_TARGET_MON_ID_0 0x06
#DEFINE VAR_MOVE_BASE_POWER 0x30

    push    {r3-r7, lr}
    mov     r5, r1
    mov     r7, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r7, r0
    bne     Return
    
    mov     r0, #VAR_TARGET_MON_ID_0
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    
    mov     r0, r5
    mov     r1, r7
    mov     r2, r6
    bl      Battle::HandlerCommon_CheckIfCanStealPokeItem
    cmp     r0, #0
    bne     Return
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::GetPoke
    bl      Battle::GetPokeHeldItem
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_MOVE_BASE_POWER
    bl      Battle::EventVar_GetValue
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
    bl      Battle::FixedRound
    mov     r1, r0
    mov     r0, #VAR_MOVE_BASE_POWER
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r3-r7, pc}
    