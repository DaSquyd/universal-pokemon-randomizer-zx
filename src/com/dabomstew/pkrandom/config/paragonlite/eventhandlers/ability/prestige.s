; Moxie but for Sp. Atk
    push    {r3-r7, lr}
    mov     r5, r1
    mov     r6, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r6, r0
    bne     Return
    
    mov     r0, #VAR_TargetCount
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    beq     Return
    
    mov     r4, #0 ; increment
    
Loop_Start:
    add     r0, r4, #VAR_TargetPokeId_0
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    beq     Loop_CheckContinue
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r6
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    ldr     r0, [r1, #HandlerParam_ChangeStatStage.header]
    mov     r2, =BHP_AbilityPopup
    orr     r0, r2
    str     r0, [r1, #HandlerParam_ChangeStatStage.header]
    
    mov     r0, #STSG_SpAtk
    str     r0, [r1, #0x04]
    
    mov     r0, #1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.amount]
    strb    r0, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    
    strb    r6, [r1, #HandlerParam_ChangeStatStage.pokeId]
    
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Loop_CheckContinue:
    add     r4, #1
    cmp     r4, r7
    bcc     Loop_Start
    
Return:
    pop     {r3-r7, pc}
