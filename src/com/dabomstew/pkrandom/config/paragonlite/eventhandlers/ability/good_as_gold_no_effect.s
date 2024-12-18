    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ; ignore self-target
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    beq     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveCategory
    cmp     r0, #CAT_Status
    bne     Return
    
    mov     r0, #VAR_NoEffectFlag
    mov     r1, #TRUE
    bl      Battle::EventVar_RewriteValue
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #HE_Message
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r6, r0
    
    add     r0, r6, #HandlerParam_Message.exStr
    mov     r1, #2
    mov     r2, #210 ; "It doesn't affect [poke]..."
    bl      Battle::Handler_StrSetup
    
    add     r0, r6, #HandlerParam_Message.exStr
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    pop     {r4-r6, pc}