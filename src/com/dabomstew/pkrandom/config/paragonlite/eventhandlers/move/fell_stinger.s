    push    {r3-r7, lr}
    mov     r5, r1
    mov     r6, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r6, r0
    bne     Return
    
    mov     r0, #VAR_TargetCount
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    beq     Return
    
    mov     r0, #VAR_TargetPokeId_0
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    beq     Return
    
    mov     r0, r5
    mov     r1, #HE_ChangeStatStage
    mov     r2, r6
    bl      Battle::Handler_PushWork
    mov     r1, r0
    
    mov     r0, #STSG_Attack
    str     r0, [r1, #HandlerParam_ChangeStatStage.stat]
    strb    r0, [r1, #HandlerParam_ChangeStatStage.pokeCount]
    mov     r0, #3
    strb    r0, [r1, #HandlerParam_ChangeStatStage.amount]
    strb    r6, [r1, #(HandlerParam_ChangeStatStage.pokeIds[0])]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r7, pc}