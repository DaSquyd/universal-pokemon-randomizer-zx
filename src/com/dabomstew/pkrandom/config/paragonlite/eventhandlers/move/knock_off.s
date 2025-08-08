    push    {r3-r7, lr}
    mov     r5, r1
    mov     r7, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r7, r0
    bne     Return
    
    mov     r0, #VAR_TargetPokeId_0
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    
    mov     r0, r5
    mov     r1, r7
    mov     r2, r6
    bl      Battle::HandlerCommon_CheckIfCanStealPokeItem
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::GetPoke
    bl      Battle::Poke_GetHeldItem
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_MovePower
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r3-r7, pc}
    