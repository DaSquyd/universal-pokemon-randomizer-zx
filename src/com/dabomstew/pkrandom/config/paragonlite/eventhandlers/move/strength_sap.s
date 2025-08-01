    push    {r4-r7, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_DefendingPoke
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    
    mov     r1, r6
    mov     r0, r5
    bl      Battle::GetPoke
    mov     r1, #STSG_Attack
    sub     r2, r1, #(STSG_Attack + 1)
    bl      Battle::IsStatChangeValid
    cmp     r0, #FALSE
    beq     Return
    
    mov     r0, r6
    mov     r1, #BPV_AttackStat
    bl      Battle::GetPokeStat
    mov     r7, r0
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r6
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    mov     r0, #STSG_Attack
    str     r0, [r1, #HandlerParam_ChangeStatStage.stat]
    sub     r0, #(STSG_Attack + 1) ; -1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.amount]
    mov     r0, #1
    strb    r0, [r1, #HandlerParam_ChangeStatStage.fAlways]
    mov     r0, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    mov     r0, r6
    mov     r0, [r1, #HandlerParam_ChangeStatStage.pokeId]
    
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    bl      Battle::Handler_Result
    cmp     r0, #HR_Success
    bne     Return
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    mov     r2, r0
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    bl      Battle::Handler_PushWork
    
    ; TODO: Handle Liquid Ooze
    
Return:
    pop     {r4-r7, pc}