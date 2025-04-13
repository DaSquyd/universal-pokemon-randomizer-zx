    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r6, r0
    bl      Battle::Poke_IsFullHP
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, r5
    mov     r1, #HE_RecoverHP
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    ldr     r0, [r7, #HandlerParam_RecoverHP.header]
    mov     r1, #(BHP_AbilityPopup >> 16)
    lsl     r1, #16
    orr     r0, r1
    str     r0, [r7, #HandlerParam_RecoverHP.header]
    
    mov     r0, r6
    mov     r1, #ABILITY_TRUANT_HEAL_FRACTION
    bl      Battle::DivideMaxHPZeroCheck
    strh    r0, [r7, #HandlerParam_RecoverHP.amount]
    
    strb    r4, [r7, #HandlerParam_RecoverHP.pokeId]
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r7, pc}