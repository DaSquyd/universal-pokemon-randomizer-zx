    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ldr     r0, [r6, #0x04]
    cmp     r0, #FALSE
    bne     RunMessageAndHeal
    
    
    ; failed for a different reason...
    mov     r0, #VAR_FailCause
    bl      Battle::EventVar_GetValue
    cmp     r0, #MFC_Flinch
    bne     Return
    
    mov     r0, #TRUE
    str     r0, [r6, #0x00]
    b       Return
    
    
RunMessageAndHeal:
#if PARAGONLITE
    ; NEW - Add Ability Popup
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r4
    bl      Battle::Handler_PushRun
    ; ~NEW
#endif
    
    
    mov     r0, r5
    mov     r1, #HE_Message
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
#if !PARAGONLITE
    ldr     r0, [r7, #HandlerParam_RecoverHP.header]
    mov     r1, #(BHP_AbilityPopup >> 16)
    lsl     r1, #16
    orr     r0, r1
    str     r0, [r7, #HandlerParam_RecoverHP.header]
#endif
    
    add     r0, r7, #HandlerParam_Message.exStr
    mov     r1, #2
    ldr     r2, =445 ; "[poke] is loafing around!"
    bl      Battle::Handler_StrSetup
    
    add     r0, r7, #HandlerParam_Message.exStr
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
    
#if PARAGONLITE
    ; NEW - Heal
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::Poke_IsFullHP
    cmp     r0, #FALSE
    bne     RemoveAbilityPopup
    
    mov     r0, r5
    mov     r1, #HE_RecoverHP
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r1, #2 ; 1/2
    bl      Battle::DivideMaxHPZeroCheck
    strh    r0, [r7, #HandlerParam_RecoverHP.amount]
    
    strb    r4, [r7, #HandlerParam_RecoverHP.pokeId]
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    ; ~NEW
    
    
    ; NEW - Remove Ability Popup
RemoveAbilityPopup:
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r4
    bl      Battle::Handler_PushRun
    ; ~NEW
#endif
    
    
    mov     r0, #FALSE
    str     r0, [r6, #0x04]
    
Return:
    pop     {r3-r7, pc}