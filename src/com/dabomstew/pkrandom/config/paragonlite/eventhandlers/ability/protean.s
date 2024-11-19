    push    {r3-r7, lr}
    sub     sp, #0x04
    
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
        
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    str     r0, [sp]
    cmp     r0, #TYPE_Null ; no type, case of Struggle
    beq     Return
    
#if !REDUX
    ; Check if it was already activated once
    ldr     r0, [r3]
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #1
    str     r0, [r3]
#endif
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #HE_ChangeType
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    ldr     r0, [sp]
    bl      Battle::TypePair_MakeMono
    strh    r0, [r7, #HandlerParam_ChangeType.type]
    strb    r4, [r7, #HandlerParam_ChangeType.pokeId]
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    add     sp, #0x04
    pop     {r3-r7, pc}