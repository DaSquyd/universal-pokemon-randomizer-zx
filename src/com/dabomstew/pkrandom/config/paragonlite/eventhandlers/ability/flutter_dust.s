    push    {r4-r7, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_SubstituteFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #VAR_ShieldDustFlag
    bl      Battle::EventVar_GetValue
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r6, r0
    mov     r1, #2
    bl      Battle::DivideMaxHP
    mov     r7, r0
    
    mov     r0, r6
    mov     r1, #BPV_CurrentHP
    bl      Battle::GetPokeStat
    cmp     r0, r7
    bhi     Return
    
    ; push
    mov     r0, r5
    mov     r1, #HE_AddCondition
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r6, r0
    
    ; TODO: make this an actual struct
    ldr     r0, #(BHP_AbilityPopup >> 16)
    lsl     r0, #16
    ldr     r1, [r6, #0x00]
    orr     r0, r1
    str     r0, [r6, #0x00]
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    strb    r0, [r6, #0x0F]
    
    mov     r0, #MC_Poison
    str     r0, [r6, #0x04]
    bl      Battle::MakeNonVolatileStatus
    str     r0, [r6, #0x08]
    
    mov     r0, r6
    add     r0, #0x14
    mov     r1, #2
    ldr     r2, =BTLTXT_FlutterDust_Activate
    bl      Battle::Handler_StrSetup
    
    mov     r0, r6
    add     r0, #0x14
    ldrb    r1, [r6, #0x0F]
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r7, pc}